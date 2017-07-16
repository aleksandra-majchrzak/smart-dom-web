import controllers.ModulesController;
import controllers.RoomsController;
import database.DatabaseManager;
import database.entities.User;
import org.mongodb.morphia.query.Query;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created by Mohru on 15.07.2017.
 */
public class WebApp {

    public static void initialize() {

        // default port 4567
        staticFileLocation("/public");

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "/public/index.vm");
        }, new VelocityTemplateEngine());

        get("/index3", (req, res) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("test", "Hey there :)");

            List<Row> rows = new ArrayList<Row>();
            rows.add(new Row("text1"));
            rows.add(new Row("text2"));
            rows.add(new Row("text3"));
            model.put("rows", rows);

            return new ModelAndView(model, "/public/index3.vm");
        }, new VelocityTemplateEngine());

        post("/signIn", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            System.out.println(request.body());

            System.out.println("username: " + username);
            System.out.println("password: " + password);

            Query<User> userQuery = DatabaseManager.getDataStore().createQuery(User.class)
                    .filter("login =", username);

            List<User> users = userQuery.asList();

            if (!users.isEmpty()) {
                User user = users.get(0);
                if (user.getPassword().equals(password)) {
                    System.out.println("Login ok");
                } else {
                    System.out.println("Wrong password");
                }
            } else {
                System.out.println("Username doesn't exist");
            }

            return "";
        });

        get("/rooms", RoomsController::getRooms,
                new VelocityTemplateEngine());

        post("/rooms", RoomsController::saveRoom,
                new VelocityTemplateEngine());

        get("/rooms/new", RoomsController::newRoom,
                new VelocityTemplateEngine());

        get("/rooms/:id", RoomsController::getRoom,
                new VelocityTemplateEngine());


        get("/modules", ModulesController::getModules,
                new VelocityTemplateEngine());

        post("/modules", ModulesController::saveModule,
                new VelocityTemplateEngine());

        get("/modules/new", ModulesController::newModule,
                new VelocityTemplateEngine());

        get("/modules/:id", ModulesController::getModule,
                new VelocityTemplateEngine());
    }
}
