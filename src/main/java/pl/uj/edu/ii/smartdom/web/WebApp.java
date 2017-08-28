package pl.uj.edu.ii.smartdom.web;

import pl.uj.edu.ii.smartdom.web.controllers.ModulesController;
import pl.uj.edu.ii.smartdom.web.controllers.RoomsController;
import pl.uj.edu.ii.smartdom.web.controllers.SmartDomController;
import pl.uj.edu.ii.smartdom.web.controllers.UserController;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.*;

/**
 * Created by Mohru on 15.07.2017.
 */
public class WebApp {

    public static void initialize() {

        secure(Constants.KEYSTORE_PATH, Constants.KEYSTORE_PASS, null, null);

        // default port 4567
        staticFileLocation("/public");

        //***************  MAIN  ***************
        get("/", (request, response) ->
                render(SmartDomController.getMain(request, response)));

        get("/menu", (request, response) ->
                render(SmartDomController.getMenu(request, response)));

        get("/register", (request, response) ->
                render(SmartDomController.getRegister(request, response)));


        //***************  USER  ***************
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


        //***************  ERRORS  ***************
        notFound((request, response) ->
                render(SmartDomController.getNotFound(request, response)));

        internalServerError((request, response) ->
                render(SmartDomController.getServerError(request, response)));
    }

    private static String render(ModelAndView modelAndView) {
        return new VelocityTemplateEngine().render(modelAndView);
    }

}
