import com.google.gson.Gson;
import serverEntities.*;

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

            return gson.toJson(new LoginResponse(user.getLogin(), token), LoginResponse.class);
        });


        //*****  LIGHT  *****
        post(baseURL + "/turnOnLight", (request, response) -> {
            Gson gson = new Gson();
            return gson.toJson(true, Boolean.class);
        });

        post(baseURL + "/turnOffLight", (request, response) -> {
            Gson gson = new Gson();
            return gson.toJson(true, Boolean.class);
        });

        post(baseURL + "/setStripColor", (request, response) -> {
            Gson gson = new Gson();
            return gson.toJson(true, Boolean.class);
        });


        //*****  METEO  *****
        get(baseURL + "/meteo", (request, response) -> {
            Gson gson = new Gson();

            String param = request.queryParamOrDefault("param", "");

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

            DoorResponse result = new DoorResponse();
            result.isOpen = door.isOpen;
            MobileAPI.isOpen = door.isOpen;
            return gson.toJson(result, DoorResponse.class);
        });

        get(baseURL + "/openDoor", (request, response) -> {
            Gson gson = new Gson();

            DoorResponse result = new DoorResponse();
            result.isOpen = isOpen;
            return gson.toJson(result, DoorResponse.class);
        });
    }
}
