package pl.uj.edu.ii.smartdom.web.controllers;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.Room;
import pl.uj.edu.ii.smartdom.web.database.entities.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.*;

/**
 * Created by Mohru on 16.07.2017.
 */
public class RoomsController {

    public static ModelAndView getRooms(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", req.session().attribute("username"));

        List<Room> rooms = DatabaseManager.getDataStore().find(Room.class).asList();
        model.put("panelName", "Rooms");
        model.put("rooms", rooms);
        model.put("roomCount", rooms.size());
        /*List<pl.uj.edu.ii.smartdom.web.Row> rows = new ArrayList<pl.uj.edu.ii.smartdom.web.Row>();
        rows.add(new pl.uj.edu.ii.smartdom.web.Row("text1"));
        rows.add(new pl.uj.edu.ii.smartdom.web.Row("text2"));
        rows.add(new pl.uj.edu.ii.smartdom.web.Row("text3"));
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
            model.put("room", room);
            model.put("users", DatabaseManager.getDataStore().find(User.class).asList());
        }

        return modelAndView;
    }

    public static ModelAndView addUser(Request req, Response res) {
        ModelAndView modelAndView = getRoom(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        String userToAddId = req.queryParams("userToAddId");
        Room room = (Room) model.get("room");

        Optional<ObjectId> optUser = room.getUserIds()
                .stream()
                .filter(id -> id.toHexString().equals(userToAddId))
                .findAny();

        if (!optUser.isPresent()) {
            User user = DatabaseManager.getDataStore().get(User.class, new ObjectId(userToAddId));
            room.getUserIds().add(new ObjectId(userToAddId));

            DatabaseManager.getDataStore().save(room);
            List<User> roomUsers = (List<User>) model.get("roomUsers");
            roomUsers.add(user);

            List<Room> userRooms = user.getRooms();

            if (userRooms.stream().filter(r -> r.getName().equals(room.getName())).count() == 0) {
                userRooms.add(room);

                DatabaseManager.getDataStore().save(user);
                System.out.println("saved room");
            }
        }

        return modelAndView;
    }

    public static ModelAndView removeUser(Request req, Response res) {
        ModelAndView modelAndView = getRoom(req, res);
        HashMap<String, Object> model = (HashMap<String, Object>) modelAndView.getModel();

        String userToDeleteId = req.queryParams("userToDeleteId");
        Room room = (Room) model.get("room");

        Optional<ObjectId> optUser = room.getUserIds()
                .stream()
                .filter(id -> id.toHexString().equals(userToDeleteId))
                .findAny();

        if (optUser.isPresent()) {
            room.getUserIds().remove(optUser.get());

            DatabaseManager.getDataStore().save(room);
            List<User> roomUsers = (List<User>) model.get("roomUsers");
            roomUsers.removeIf(user -> user.getId().toHexString().equals(userToDeleteId));

            User user = DatabaseManager.getDataStore().get(User.class, new ObjectId(userToDeleteId));
            List<Room> userRooms = user.getRooms();

            Optional<Room> optRoom = userRooms.stream().filter(r -> r.getName().equals(room.getName())).findAny();
            if (optRoom.isPresent()) {
                userRooms.remove(optRoom.get());

                DatabaseManager.getDataStore().save(user);
                System.out.println("removed room");
            }
        }

        return modelAndView;
    }

}
