import controllers.ModulesController;
import controllers.RoomsController;
import controllers.UserController;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
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


        //***************  USER  ***************
        get("/register", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "/public/register.vm");
        }, new VelocityTemplateEngine());

        post("/register", UserController::register,
                new VelocityTemplateEngine());

        post("/signIn", UserController::signIn,
                new VelocityTemplateEngine());

        get("/logOut", UserController::logOut);


        //***************  ROOMS  ***************
        get("/rooms", RoomsController::getRooms,
                new VelocityTemplateEngine());

        post("/rooms", RoomsController::saveRoom,
                new VelocityTemplateEngine());

        get("/rooms/new", RoomsController::newRoom,
                new VelocityTemplateEngine());

        get("/rooms/:id", RoomsController::getRoom,
                new VelocityTemplateEngine());

        get("/rooms/:id/edit", RoomsController::editRoom,
                new VelocityTemplateEngine());

        post("/rooms/:id/edit/users", RoomsController::addUser,
                new VelocityTemplateEngine());

        post("/rooms/:id/edit/users/delete", RoomsController::removeUser,
                new VelocityTemplateEngine());


        //***************  MODULES  ***************
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
