package pl.uj.edu.ii.smartdom.web.controllers;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.pac4j.core.context.Pac4jConstants;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.Module;
import pl.uj.edu.ii.smartdom.web.database.entities.Room;
import pl.uj.edu.ii.smartdom.web.database.entities.User;
import pl.uj.edu.ii.smartdom.web.utils.JwtUtils;
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
        model.put("username", req.session().attribute("username"));
        model.put("isAdmin", req.attribute("isAdmin"));
        model.put("csrfToken", req.session().attribute(Pac4jConstants.CSRF_TOKEN));

        String userId = JwtUtils.getUserIdFromToken(req.cookie("auth_token"));
        List<Room> rooms;
        if (req.attribute("isAdmin")) {
            rooms = DatabaseManager.getDataStore().find(Room.class).asList();
        } else {
            rooms = DatabaseManager.getDataStore().get(User.class, new ObjectId(userId)).getRooms();
        }

        model.put("panelName", "Rooms");
        model.put("rooms", rooms);
        model.put("roomCount", rooms.size());

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
            model.put("users", DatabaseManager.getDataStore().find(User.class).asList());
            model.put("roomUsers", DatabaseManager.getDataStore().find(User.class).filter("_id in ", room.getUserIds()).asList());
        }

        return modelAndView;
    }

    public static ModelAndView editRoom(Request req, Response res) {
        ModelAndView modelAndView = getRooms(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        String id = req.params(":id");
        Room room = DatabaseManager.getDataStore().get(Room.class, new ObjectId(id));

        if (room != null) {
            model.put("roomToEdit", room);
            model.put("users", DatabaseManager.getDataStore().find(User.class).asList());
        }

        return modelAndView;
    }

    public static ModelAndView saveEditedRoom(Request req, Response res) {
        String id = req.params(":id");
        Room room = DatabaseManager.getDataStore().get(Room.class, new ObjectId(id));
        String roomName = req.queryParams("name");
        Query<Room> roomQuery = DatabaseManager.getDataStore().find(Room.class, "name", roomName);

        ModelAndView modelAndView = editRoom(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        if (roomQuery.asList().isEmpty()) {
            if (roomName.isEmpty()) {
                model.put("errors", Collections.singletonList("Room name cannot be empty."));
            } else {
                room.setName(roomName);
                DatabaseManager.getDataStore().save(room);
                res.redirect("/rooms/" + room.getId().toHexString());
                return null;
            }
        } else {
            model.put("errors", Collections.singletonList("Room name already exists."));
        }

        return modelAndView;
    }

    public static ModelAndView addUser(Request req, Response res) {
        String roomId = req.params(":id");
        String userToAddId = req.queryParams("userToAddId");
        Room room = DatabaseManager.getDataStore().get(Room.class, new ObjectId(roomId));
        User user = DatabaseManager.getDataStore().get(User.class, new ObjectId(userToAddId));

        if (user != null) {
            Datastore ds = DatabaseManager.getDataStore();
            ds.update(room, ds.createUpdateOperations(Room.class).add("userIds", userToAddId));
            ds.update(user, ds.createUpdateOperations(User.class).add("rooms", room));
            System.out.println("saved room");
        }

        res.redirect("/rooms/" + room.getId().toHexString());
        return null;
    }

    public static ModelAndView removeUser(Request req, Response res) {
        String roomId = req.params(":id");
        String userToDeleteId = req.queryParams("userToDeleteId");
        Room room = DatabaseManager.getDataStore().get(Room.class, new ObjectId(roomId));
        User user = DatabaseManager.getDataStore().get(User.class, new ObjectId(userToDeleteId));

        if (user != null) {
            Datastore ds = DatabaseManager.getDataStore();
            ds.update(user, ds.createUpdateOperations(User.class).removeAll("rooms", room));
            ds.update(room, ds.createUpdateOperations(Room.class).removeAll("userIds", user.getId().toString()));
            System.out.println("removed user");
        }
        res.redirect("/rooms/" + room.getId().toHexString());
        return null;
    }

    public static ModelAndView deleteRoom(Request req, Response res) {
        String roomId = req.params(":id");
        Room room = DatabaseManager.getDataStore().get(Room.class, new ObjectId(roomId));

        if (room != null) {
            Datastore ds = DatabaseManager.getDataStore();
            ds.update(ds.find(User.class), ds.createUpdateOperations(User.class).removeAll("rooms", room));
            ds.update(ds.find(Module.class, "room", roomId), ds.createUpdateOperations(Module.class).unset("room"));
            ds.delete(room);
            System.out.println("deleted room");
        }
        res.redirect("/rooms/" + roomId);
        return null;
    }
}
