package pl.uj.edu.ii.smartdom.web.controllers;

import org.mongodb.morphia.query.Query;
import pl.uj.edu.ii.smartdom.web.JmDNSManager;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.Module;
import pl.uj.edu.ii.smartdom.web.database.entities.Room;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.util.*;

/**
 * Created by Mohru on 16.07.2017.
 */
public class ModulesController {

    public static ModelAndView getModules(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();

        List<Module> modules = new ArrayList<>();
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

        Query<Room> roomQuery = DatabaseManager.getDataStore().find(Room.class, "name", moduleName);

        ModelAndView modelAndView = getModules(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        if (roomQuery.asList().isEmpty()) {

            if (moduleName.isEmpty()) {
                model.put("errors", Collections.singletonList("Module name cannot be empty."));
            } else {
                DatabaseManager.getDataStore().save(new Module(moduleName) {
                });
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

        model.put("moduleId", 1);

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
