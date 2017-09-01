package pl.uj.edu.ii.smartdom.web.controllers;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import pl.uj.edu.ii.smartdom.web.JmDNSManager;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.Module;
import pl.uj.edu.ii.smartdom.web.database.entities.Room;
import pl.uj.edu.ii.smartdom.web.enums.ModuleType;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohru on 16.07.2017.
 */
public class ModulesController {

    public static ModelAndView getModules(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", req.session().attribute("username"));

        List<Module> modules = DatabaseManager.getDataStore().find(Module.class).asList();
        model.put("panelName", "Modules");
        model.put("modules", modules);
        model.put("moduleCount", modules.size());

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
        String moduleName = req.queryParams("name");

        System.out.println(req.body());

        Query<Module> ModuleQuery = DatabaseManager.getDataStore().find(Module.class, "name", moduleName);

        ModelAndView modelAndView = getModules(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        if (ModuleQuery.asList().isEmpty()) {

            if (moduleName.isEmpty()) {
                model.put("errors", Collections.singletonList("Module name cannot be empty."));
            } else if (req.queryParams("port").isEmpty()) {
                model.put("errors", Collections.singletonList("Module port cannot be empty."));
            } else {
                Integer port = Integer.valueOf(req.queryParams("port"));
                String type = req.queryParams("type");
                String roomId = req.queryParams("roomId");
                Module module = new Module(moduleName, ModuleType.valueOf(type), port);
                Room room = DatabaseManager.getDataStore().get(Room.class, new ObjectId(roomId));
                module.setRoom(room);
                DatabaseManager.getDataStore().save(module);
                if (room.getModules().stream().filter(m -> m.getName().equals(moduleName)).count() == 0) {
                    room.getModules().add(module);
                    DatabaseManager.getDataStore().save(room);
                }
                modelAndView = getModules(req, res);
            }
        } else {
            model.put("errors", Collections.singletonList("Module name already exists."));
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

    private static ServiceListener serviceListener = new ServiceListener() {
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
        }
    };
}
