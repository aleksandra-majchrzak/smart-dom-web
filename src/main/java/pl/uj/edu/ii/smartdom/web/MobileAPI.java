package pl.uj.edu.ii.smartdom.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mongodb.morphia.query.Query;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.serverEntities.*;
import pl.uj.edu.ii.smartdom.web.utils.ModuleUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

            User user = gson.fromJson(req.body(), User.class);

            System.out.println("username: " + user.getLogin());
            System.out.println("password: " + user.getPassword());

            // znajdz narzedze do generowania tokenow ?? - ogolnie do autentykacji
            String token = "abcdefg";

            String jwt = Jwts.builder()
                    .setSubject("users/TzMUocMF4p")
                    .setExpiration(new Date(1300819380))
                    .claim("name", "Robert Token Man")
                    .claim("scope", "self groups/admins")
                    .signWith(
                            SignatureAlgorithm.HS256,
                            "secret".getBytes("UTF-8")
                    )
                    .compact();

            return gson.toJson(new LoginResponse(user.getLogin(), token), LoginResponse.class);
        });

        //*****  ROOMS  ******
        get(baseURL + "/rooms", (request, response) -> {
            Gson gson = new Gson();

            String authenToken = request.headers("Authorization");
            String login = request.queryParams("login");

            // todo autentykacja

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

            //String newURL = "http://192.168.0.52:80/turnOnLight";
            String newURL = "http://192.168.0.248:80/turnOnLight";
            HttpURLConnection connection = ModuleUtils.getPostConnection(newURL);
            response.raw().setStatus(connection.getResponseCode());

            return gson.toJson(true, Boolean.class);
        });

        post(baseURL + "/turnOffLight", (request, response) -> {
            Gson gson = new Gson();
            Light light = gson.fromJson(request.body(), Light.class);
            System.out.println("module id: " + light.getServerId());

            //String newURL = "http://192.168.0.52:80/turnOffLight";
            String newURL = "http://192.168.0.248:80/turnOffLight";
            HttpURLConnection connection = ModuleUtils.getPostConnection(newURL);
            response.raw().setStatus(connection.getResponseCode());

            return gson.toJson(true, Boolean.class);
        });

        post(baseURL + "/setStripColor", (request, response) -> {
            Gson gson = new Gson();
            Light light = gson.fromJson(request.body(), Light.class);
            System.out.println("module id: " + light.getServerId());

            String newURL = "http://192.168.0.248:80/setStripColor";
            HttpURLConnection connection = ModuleUtils.getPostConnection(newURL);
            OutputStream output1 = connection.getOutputStream();

            System.out.println(request.body());

            Map<String, Integer> rgb = light.getRgb();
            JsonObject obj = new JsonObject();
            obj.addProperty("red", rgb.get("red"));
            obj.addProperty("green", rgb.get("green"));
            obj.addProperty("blue", rgb.get("blue"));
            output1.write(obj.toString().getBytes());

            response.raw().setStatus(connection.getResponseCode());

            return gson.toJson(true, Boolean.class);
        });

        post(baseURL + "/setStripBrightness", (request, response) -> {
            Gson gson = new Gson();
            Light light = gson.fromJson(request.body(), Light.class);
            System.out.println("module id: " + light.getServerId());

            String newURL = "http://192.168.0.248:80/setStripBrightness";
            HttpURLConnection connection = ModuleUtils.getPostConnection(newURL);
            OutputStream output1 = connection.getOutputStream();

            System.out.println(request.body());

            JsonObject obj = new JsonObject();
            obj.addProperty("brightness", light.getBrightness());
            output1.write(obj.toString().getBytes());

            response.raw().setStatus(connection.getResponseCode());

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
                    result.co2 = 10;
                    break;
                case "co":
                    result.co = 11;
                    break;
                case "gas":
                    result.gas = 12;
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

            String newURL = "http://192.168.0.52:81/openBlind";
            HttpURLConnection connection = ModuleUtils.getPostConnection(newURL);
            OutputStream output1 = connection.getOutputStream();

            JsonObject obj = new JsonObject();
            obj.addProperty("start", blind.isShouldStart());
            output1.write(obj.toString().getBytes());

            response.raw().setStatus(connection.getResponseCode());

            return gson.toJson(true, Boolean.class);
        });

        post(baseURL + "/closeBlind", (request, response) -> {
            Gson gson = new Gson();

            Blind blind = gson.fromJson(request.body(), Blind.class);
            System.out.println("blind id: " + blind.getServerId());

            System.out.println(request.body());

            String newURL = "http://192.168.0.52:81/closeBlind";
            HttpURLConnection connection = ModuleUtils.getPostConnection(newURL);
            OutputStream output1 = connection.getOutputStream();

            JsonObject obj = new JsonObject();
            obj.addProperty("start", blind.isShouldStart());
            output1.write(obj.toString().getBytes());

            response.raw().setStatus(connection.getResponseCode());

            return gson.toJson(true, Boolean.class);
        });
    }


}
