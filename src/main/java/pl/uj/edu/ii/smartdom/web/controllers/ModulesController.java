package pl.uj.edu.ii.smartdom.web.controllers;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.pac4j.core.context.Pac4jConstants;
import pl.uj.edu.ii.smartdom.web.Constants;
import pl.uj.edu.ii.smartdom.web.JmDNSManager;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.Module;
import pl.uj.edu.ii.smartdom.web.database.entities.Room;
import pl.uj.edu.ii.smartdom.web.database.entities.User;
import pl.uj.edu.ii.smartdom.web.enums.ConnectionType;
import pl.uj.edu.ii.smartdom.web.enums.ModuleType;
import pl.uj.edu.ii.smartdom.web.utils.JmDNSService;
import pl.uj.edu.ii.smartdom.web.utils.JwtUtils;
import pl.uj.edu.ii.smartdom.web.utils.ModuleUtils;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.*;

/**
 * Created by Mohru on 16.07.2017.
 */
public class ModulesController {

    private static List<JmDNSService> services = new ArrayList<>();

    public static ModelAndView getModules(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", req.session().attribute("username"));
        model.put("isAdmin", req.attribute("isAdmin"));
        model.put("csrfToken", req.session().attribute(Pac4jConstants.CSRF_TOKEN));

        List<Module> modules;

        if (req.attribute("isAdmin")) {
            modules = DatabaseManager.getDataStore().find(Module.class).asList();
        } else {
            String userId = JwtUtils.getUserIdFromToken(req.cookie("auth_token"));
            User user = DatabaseManager.getDataStore().get(User.class, new ObjectId(userId));
            modules = DatabaseManager.getDataStore().find(Module.class)
                    .field("room").in(user.getRooms()).asList();
        }

        model.put("panelName", "Modules");
        model.put("modules", modules);
        model.put("moduleCount", modules.size());
        model.put("services", services);
        model.put("serviceCount", services.size());

        JmDNSManager.startJmDNS(serviceListener);
        model.put("serverAddress", JmDNSManager.getAddress());

        List<String> errors = req.session().attribute("errors");
        if (errors != null) {
            model.put("errors", errors);
            req.session().removeAttribute("errors");
        }

        return new ModelAndView(model, "/public/modules/modules.vm");
    }

    public static ModelAndView newModule(Request req, Response res) {
        ModelAndView modelAndView = getModules(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        model.put("addNewModule", true);
        List<Room> rooms = DatabaseManager.getDataStore().find(Room.class).asList();
        model.put("rooms", rooms);

        return modelAndView;
    }

    public static ModelAndView saveModule(Request req, Response res) {
        String moduleName = req.queryParams("moduleToConnectName");
        String error = null;
        Optional<JmDNSService> serviceOpt = services.stream().filter(s -> s.getName().equals(moduleName)).findFirst();

        if (serviceOpt.isPresent()) {
            JmDNSService service = serviceOpt.get();
            Module module = new Module(service.getName(),
                    ModuleType.UNKNOWN,
                    service.getAddress(),
                    service.getPort(), ConnectionType.WI_FI);

            if (DatabaseManager.getDataStore().find(Module.class, "name", service.getName()).countAll() == 0) {
                HttpURLConnection connection = ModuleUtils.getGetConnection(module, "type");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String type = reader.readLine();
                    module.setType(ModuleType.valueOf(String.valueOf(type)));

                    DatabaseManager.getDataStore().save(module);
                    service.setConnected(true);

                    res.redirect("/modules/" + module.getId().toHexString());
                    return null;

                } catch (IOException e) {
                    e.printStackTrace();
                    error = "Nie można połaczyć z modułem.";
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    error = "Nieznany rodzaj modułu.";
                }
            } else {
                error = "Nie można połaczyć z modułem.";
            }
        }

        if (error != null) {
            req.session().attribute("errors", Collections.singleton(error));
        }
        res.redirect("/modules");
        return null;
    }

    public static ModelAndView saveBleModule(Request req, Response res) {

        String name = req.queryParams("name");
        String address = req.queryParams("address");
        //String type = req.queryParams("type");
        String roomId = req.queryParams("roomId");
        Datastore ds = DatabaseManager.getDataStore();
        Room room = ds.get(Room.class, new ObjectId(roomId));

        if (ds.find(Module.class, "address", address).countAll() > 0) {
            req.session().attribute("errors", Collections.singleton("Moduł o tym adresie już istnieje."));
        } else {
            Module newModule = new Module(name, ModuleType.METEO_MODULE, address, 0, ConnectionType.BLE);
            newModule.setRoom(room);
            ds.save(newModule);
            if (room != null) {
                ds.update(room, ds.createUpdateOperations(Room.class).add("modules", newModule));
            }
        }
        res.redirect("/modules");
        return null;
    }

    public static ModelAndView getModule(Request req, Response res) {
        ModelAndView modelAndView = getModules(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        String id = req.params(":id");
        Module module = DatabaseManager.getDataStore().get(Module.class, new ObjectId(id));

        if (module != null) {
            model.put("module", module);
            model.put("room", module.getRoom());
        }

        return modelAndView;
    }

    public static ModelAndView deleteModule(Request req, Response res) {
        String id = req.params(":id");
        Module module = DatabaseManager.getDataStore().get(Module.class, new ObjectId(id));

        if (module != null) {
            DatabaseManager.getDataStore().delete(module);

            services.stream().filter(s -> s.getName().equals(module.getName()))
                    .findAny()
                    .ifPresent(s -> s.setConnected(false));

            if (module.getRoom() != null) {
                Datastore ds = DatabaseManager.getDataStore();
                ds.update(module.getRoom(), ds.createUpdateOperations(Room.class).removeAll("modules", module));
            }
        }

        res.redirect("/modules");
        return null;
    }

    public static ModelAndView editModule(Request req, Response res) {
        ModelAndView modelAndView = getModule(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        if (model.get("module") != null) {
            model.put("editModule", true);
            List<Room> rooms = DatabaseManager.getDataStore().find(Room.class).asList();
            model.put("rooms", rooms);
            rooms.forEach(r -> System.out.println(r.getId()));

            return modelAndView;
        }

        res.redirect("/modules");
        return null;
    }

    public static ModelAndView saveEditedModule(Request req, Response res) {
        ModelAndView modelAndView = getModule(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        if (model.get("module") != null) {
            Module editedModule = (Module) model.get("module");
            String roomId = req.queryParams("roomId");
            Datastore ds = DatabaseManager.getDataStore();
            Room room = ds.get(Room.class, new ObjectId(roomId));
            if (editedModule.getRoom() != null) {
                ds.update(editedModule.getRoom(), ds.createUpdateOperations(Room.class).removeAll("modules", editedModule));
            }
            if (room != null) {
                editedModule.setRoom(room);
                ds.save(editedModule);
                ds.update(room, ds.createUpdateOperations(Room.class).add("modules", editedModule));
            }

            return modelAndView;
        }

        res.redirect("/modules");
        return null;
    }

    private static ServiceListener serviceListener = new ServiceListener() {
        @Override
        public void serviceAdded(ServiceEvent event) {
            //System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
            services.stream().filter(s -> s.getName().equals(event.getName()))
                    .findAny()
                    .ifPresent(serviceEvent -> services.remove(serviceEvent));
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());

            if (!event.getName().startsWith(Constants.SMART_DOM))
                return;

            if (services.stream().noneMatch(s -> s.getName().equals(event.getName()))) {
                Query<Module> moduleQuery = DatabaseManager.getDataStore().find(Module.class, "name", event.getName());

                JmDNSService newService = new JmDNSService(event.getName(),
                        event.getInfo().getInet4Addresses()[0].getHostAddress(),
                        event.getInfo().getPort(),
                        moduleQuery.countAll() > 0);
                services.add(newService);
            }
        }
    };
}
