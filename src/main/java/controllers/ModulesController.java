package controllers;

import database.DatabaseManager;
import database.entities.Module;
import database.entities.Room;
import org.mongodb.morphia.query.Query;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

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
        /*List<Row> rows = new ArrayList<Row>();
        rows.add(new Row("text1"));
        rows.add(new Row("text2"));
        rows.add(new Row("text3"));
        model.put("rows", rows);*/

        return new ModelAndView(model, "/public/rooms/rooms.vm");
    }

    public static ModelAndView newModule(Request req, Response res) {
        ModelAndView modelAndView = getModules(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        model.put("addNewModule", true);

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
}
