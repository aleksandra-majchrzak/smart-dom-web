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

        secure(Constants.KEYSTORE_PATH, Constants.KEYSTORE_PASS, null, null);

        // default port 4567
        staticFileLocation("/public");

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return render(new ModelAndView(model, "/public/index.vm"));
        });


        //***************  USER  ***************
        get("/register", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return render(new ModelAndView(model, "/public/register.vm"));
        });

        post("/register", (request, response) ->
                render(UserController.register(request, response)));

        post("/signIn", (request, response) ->
                render(UserController.signIn(request, response)));

        get("/logOut", (request, response) ->
                render(UserController.logOut(request, response)));


        //***************  ROOMS  ***************
        get("/rooms", (request, response) ->
                render(RoomsController.getRooms(request, response)));

        post("/rooms", (request, response) ->
                render(RoomsController.saveRoom(request, response)));

        get("/rooms/new", (request, response) ->
                render(RoomsController.newRoom(request, response)));

        get("/rooms/:id", (request, response) ->
                render(RoomsController.getRoom(request, response)));

        get("/rooms/:id/edit", (request, response) ->
                render(RoomsController.editRoom(request, response)));

        post("/rooms/:id/edit/users", (request, response) ->
                render(RoomsController.addUser(request, response)));

        post("/rooms/:id/edit/users/delete", (request, response) ->
                render(RoomsController.removeUser(request, response)));


        //***************  MODULES  ***************
        get("/modules", (request, response) ->
                render(ModulesController.getModules(request, response)));

        post("/modules", (request, response) ->
                render(ModulesController.saveModule(request, response)));

        get("/modules/new", (request, response) ->
                render(ModulesController.newModule(request, response)));

        get("/modules/:id", (request, response) ->
                render(ModulesController.getModule(request, response)));
    }

    private static String render(ModelAndView modelAndView) {
        return new VelocityTemplateEngine().render(modelAndView);
    }
}
