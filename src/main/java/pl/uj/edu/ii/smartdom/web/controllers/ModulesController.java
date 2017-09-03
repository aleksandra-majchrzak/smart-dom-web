package pl.uj.edu.ii.smartdom.web.controllers;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import pl.uj.edu.ii.smartdom.web.Constants;
import pl.uj.edu.ii.smartdom.web.JmDNSManager;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.Module;
import pl.uj.edu.ii.smartdom.web.database.entities.Room;
import pl.uj.edu.ii.smartdom.web.enums.ModuleType;
import pl.uj.edu.ii.smartdom.web.utils.JmDNSService;
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

        List<Module> modules = DatabaseManager.getDataStore().find(Module.class).asList();
        model.put("panelName", "Modules");
        model.put("modules", modules);
        model.put("moduleCount", modules.size());
        model.put("services", services);
        model.put("serviceCount", services.size());

        JmDNSManager.startJmDNS(serviceListener);

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
                    service.getPort());

            if (DatabaseManager.getDataStore().find(Module.class, "name", service.getName()).countAll() == 0) {
                //todo to powinno byc laczenie z modulem - pobieranie typu, ustawianie polaczenia

                String newURL = ModuleUtils.getModuleRequestURL(module, "type");
                HttpURLConnection connection = ModuleUtils.getGetConnection(newURL);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String type = reader.readLine();
                    module.setType(ModuleType.valueOf(String.valueOf(type)));

                    DatabaseManager.getDataStore().save(module);
                    service.setConnected(true);

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

        ModelAndView modelAndView = getModules(req, res);
        if (error != null) {
            HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();
            model.put("errors", Collections.singleton(error));
        }

        return modelAndView;
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
                Room room = DatabaseManager.getDataStore().get(Room.class, module.getRoom().getId());
                room.getModules().removeIf(m -> m.getName().equals(module.getName()));
                DatabaseManager.getDataStore().save(room);
            }
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
