package controllers;

import database.DatabaseManager;
import database.entities.Room;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohru on 16.07.2017.
 */
public class RoomsController {

    public static ModelAndView getRooms(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();

        List<Room> rooms = DatabaseManager.getDataStore().find(Room.class).asList();
        model.put("panelName", "Rooms");
        model.put("rooms", rooms);
        model.put("roomCount", rooms.size());
        /*List<Row> rows = new ArrayList<Row>();
        rows.add(new Row("text1"));
        rows.add(new Row("text2"));
        rows.add(new Row("text3"));
        model.put("rows", rows);*/

        return new ModelAndView(model, "/public/rooms/rooms.vm");
    }

    public static ModelAndView newRoom(Request req, Response res) {
        ModelAndView modelAndView = getRooms(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        model.put("addNewRoom", true);

        return modelAndView;
    }

    public static ModelAndView saveRoom(Request req, Response res) {
        String roomName = req.queryParams("name");

        System.out.println(req.body());

        Query<Room> roomQuery = DatabaseManager.getDataStore().find(Room.class, "name", roomName);

        ModelAndView modelAndView = getRooms(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        if (roomQuery.asList().isEmpty()) {

            if (roomName.isEmpty()) {
                model.put("errors", Collections.singletonList("Room name cannot be empty."));
            } else {
                DatabaseManager.getDataStore().save(new Room(roomName));
                modelAndView = getRooms(req, res);
            }
        } else {
            model.put("errors", Collections.singletonList("Room name already exists."));
        }

        return modelAndView;
    }

    public static ModelAndView getRoom(Request req, Response res) {
        ModelAndView modelAndView = getRooms(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        String id = req.params(":id");
        Room room = DatabaseManager.getDataStore().get(Room.class, new ObjectId(id));

        if (room != null) {
            model.put("room", room);
        }

        return modelAndView;
    }

}
