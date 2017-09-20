package pl.uj.edu.ii.smartdom.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.Module;
import pl.uj.edu.ii.smartdom.web.database.entities.User;
import pl.uj.edu.ii.smartdom.web.serverEntities.*;
import pl.uj.edu.ii.smartdom.web.utils.JwtUtils;
import pl.uj.edu.ii.smartdom.web.utils.ModuleUtils;
import pl.uj.edu.ii.smartdom.web.utils.StringUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by Mohru on 15.07.2017.
 */
public class MobileAPI {

    private static final String baseURL = "/api";

    private static boolean isOpen = true;

    public static void initialize() {

        //*****  LOGIN  *****
        post(baseURL + "/login", (req, res) -> {
            Gson gson = new Gson();

            UserEntity userEntity = gson.fromJson(req.body(), UserEntity.class);

            System.out.println("in login");

            Query<User> userQuery = DatabaseManager.getDataStore().find(User.class, "login =", userEntity.getLogin());

            List<User> users = userQuery.asList();

            if (!users.isEmpty()) {
                User user = users.get(0);
                if (user.isConfirmed()) {
                    if (user.getPassword().equals(StringUtils.getHashString(userEntity.getPassword()))) {
                        String jwt = JwtUtils.createJWT(UUID.randomUUID().toString(), "mobile", user.getId().toHexString());
                        return gson.toJson(new LoginResponse(userEntity.getLogin(), jwt), LoginResponse.class);

                    } else {
                        res.status(401);
                    }
                } else {
                    res.status(403);
                }
            } else {
                res.status(401);
            }
            return null;
        });

        //*****  ROOMS  ******
        get(baseURL + "/rooms", (request, response) -> {
            Gson gson = new Gson();
            String login = request.queryParams("login");

            Query<pl.uj.edu.ii.smartdom.web.database.entities.User> userQuery = DatabaseManager.getDataStore()
                    .find(pl.uj.edu.ii.smartdom.web.database.entities.User.class)
                    .field("login")
                    .equal(login);

            List<pl.uj.edu.ii.smartdom.web.database.entities.User> userList = userQuery.asList();
            List<RoomResponse> rooms = new ArrayList<>();

            if (!userList.isEmpty()) {
                userList.get(0).getRooms().forEach(room -> rooms.add(new RoomResponse(room)));
                return gson.toJson(rooms,
                        new TypeToken<List<RoomResponse>>() {
                        }.getType());
            } else
                return gson.toJson(rooms,
                        new TypeToken<List<RoomResponse>>() {
                        }.getType());

        });


        //*****  LIGHT  *****
        post(baseURL + "/turnOnLight", (request, response) -> {
            Gson gson = new Gson();
            Light light = gson.fromJson(request.body(), Light.class);
            System.out.println("module id: " + light.getServerId());

            Module module = DatabaseManager.getDataStore().get(Module.class, new ObjectId(light.getServerId()));

            if (module != null) {
                HttpURLConnection connection = ModuleUtils.getPostConnection(module, "turnOnLight");
                response.raw().setStatus(connection.getResponseCode());
            } else {
                response.raw().setStatus(404);
            }
            return gson.toJson(true, Boolean.class);
        });

        post(baseURL + "/turnOffLight", (request, response) -> {
            Gson gson = new Gson();
            Light light = gson.fromJson(request.body(), Light.class);
            System.out.println("module id: " + light.getServerId());

            Module module = DatabaseManager.getDataStore().get(Module.class, new ObjectId(light.getServerId()));

            if (module != null) {
                HttpURLConnection connection = ModuleUtils.getPostConnection(module, "turnOffLight");
                response.raw().setStatus(connection.getResponseCode());
            } else {
                response.raw().setStatus(404);
            }

            return gson.toJson(true, Boolean.class);
        });

        post(baseURL + "/setStripColor", (request, response) -> {
            Gson gson = new Gson();
            Light light = gson.fromJson(request.body(), Light.class);
            System.out.println("module id: " + light.getServerId());

            Module module = DatabaseManager.getDataStore().get(Module.class, new ObjectId(light.getServerId()));

            if (module != null) {
                HttpURLConnection connection = ModuleUtils.getPostConnection(module, "setStripColor");
                OutputStream output1 = connection.getOutputStream();

                System.out.println(request.body());

                Map<String, Integer> rgb = light.getRgb();
                JsonObject obj = new JsonObject();
                obj.addProperty("red", rgb.get("red"));
                obj.addProperty("green", rgb.get("green"));
                obj.addProperty("blue", rgb.get("blue"));
                output1.write(obj.toString().getBytes());

                response.raw().setStatus(connection.getResponseCode());
            } else {
                response.raw().setStatus(404);
            }


            return gson.toJson(true, Boolean.class);
        });

        post(baseURL + "/setStripBrightness", (request, response) -> {
            Gson gson = new Gson();
            Light light = gson.fromJson(request.body(), Light.class);
            System.out.println("module id: " + light.getServerId());

            Module module = DatabaseManager.getDataStore().get(Module.class, new ObjectId(light.getServerId()));

            if (module != null) {
                HttpURLConnection connection = ModuleUtils.getPostConnection(module, "setStripBrightness");
                OutputStream output1 = connection.getOutputStream();

                System.out.println(request.body());

                JsonObject obj = new JsonObject();
                obj.addProperty("brightness", light.getBrightness());
                output1.write(obj.toString().getBytes());

                response.raw().setStatus(connection.getResponseCode());
            } else {
                response.raw().setStatus(404);
            }
            return gson.toJson(true, Boolean.class);
        });


        //*****  METEO  *****
        get(baseURL + "/meteo", (request, response) -> {
            Gson gson = new Gson();

            String param = request.queryParamOrDefault("param", "");
            String id = request.queryParams("moduleServerId");
            System.out.println("module id: " + id);

            MeteoResponse result = new MeteoResponse();

            switch (param) {
                case "temperature":
                    result.temperature = 23;
                    break;
                case "humidity":
                    result.humidity = 56;
                    break;
                case "co2":
                    result.co2 = 0;
                    break;
                case "co":
                    result.co = 0;
                    break;
                case "gas":
                    result.gas = 3;
                    break;
                default:
                    break;
            }

            return gson.toJson(result, MeteoResponse.class);
        });


        //*****  DOOR  *****
        post(baseURL + "/openDoor", (request, response) -> {
            Gson gson = new Gson();

            Door door = gson.fromJson(request.body(), Door.class);
            System.out.println("door id: " + door.getServerId());

            DoorResponse result = new DoorResponse();
            result.isOpen = door.isOpen;
            MobileAPI.isOpen = door.isOpen;
            return gson.toJson(result, DoorResponse.class);
        });

        get(baseURL + "/openDoor", (request, response) -> {
            String id = request.queryParams("doorServerId");
            System.out.println("door id: " + id);

            Gson gson = new Gson();

            DoorResponse result = new DoorResponse();
            result.isOpen = isOpen;
            return gson.toJson(result, DoorResponse.class);
        });

        //*****  BLIND  *****
        post(baseURL + "/openBlind", (request, response) -> {
            Gson gson = new Gson();

            Blind blind = gson.fromJson(request.body(), Blind.class);
            System.out.println("blind id: " + blind.getServerId());

            System.out.println(request.body());

            Module module = DatabaseManager.getDataStore().get(Module.class, new ObjectId(blind.getServerId()));

            if (module != null) {
                HttpURLConnection connection = ModuleUtils.getPostConnection(module, "openBlind");
                OutputStream output1 = connection.getOutputStream();

                JsonObject obj = new JsonObject();
                obj.addProperty("start", blind.isShouldStart());
                output1.write(obj.toString().getBytes());

                response.raw().setStatus(connection.getResponseCode());
            } else {
                response.raw().setStatus(404);
            }
            return gson.toJson(true, Boolean.class);
        });

        post(baseURL + "/closeBlind", (request, response) -> {
            Gson gson = new Gson();

            Blind blind = gson.fromJson(request.body(), Blind.class);
            System.out.println("blind id: " + blind.getServerId());
            System.out.println(request.body());

            Module module = DatabaseManager.getDataStore().get(Module.class, new ObjectId(blind.getServerId()));

            if (module != null) {
                HttpURLConnection connection = ModuleUtils.getPostConnection(module, "closeBlind");
                OutputStream output1 = connection.getOutputStream();

                JsonObject obj = new JsonObject();
                obj.addProperty("start", blind.isShouldStart());
                output1.write(obj.toString().getBytes());

                response.raw().setStatus(connection.getResponseCode());
            } else {
                response.raw().setStatus(404);
            }
            return gson.toJson(true, Boolean.class);
        });
    }


}
