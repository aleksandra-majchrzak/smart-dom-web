package pl.uj.edu.ii.smartdom.web;

import org.pac4j.core.config.Config;
import org.pac4j.sparkjava.SecurityFilter;
import pl.uj.edu.ii.smartdom.web.controllers.ModulesController;
import pl.uj.edu.ii.smartdom.web.controllers.RoomsController;
import pl.uj.edu.ii.smartdom.web.controllers.SmartDomController;
import pl.uj.edu.ii.smartdom.web.controllers.UsersController;
import pl.uj.edu.ii.smartdom.web.security.SmartDomConfigFactory;
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


        //***************  USERS  ***************
        post("/register", (request, response) ->
                render(UsersController.register(request, response)));

        post("/signIn", (request, response) ->
                render(UsersController.signIn(request, response)));

        get("/logOut", (request, response) ->
                render(UsersController.logOut(request, response)));

        get("/users", (request, response) ->
                render(UsersController.getUsers(request, response)));

        post("/users/:id/activate", (request, response) ->
                render(UsersController.activateUser(request, response)));

        post("/users/:id/delete", (request, response) ->
                render(UsersController.deleteUser(request, response)));


        //***************  ROOMS  ***************
        get("/rooms", (request, response) ->
                render(RoomsController.getRooms(request, response)));

        post("/rooms", (request, response) ->
                render(RoomsController.saveRoom(request, response)));

        get("/rooms/new", (request, response) ->
                render(RoomsController.newRoom(request, response)));

        get("/rooms/:id", (request, response) ->
                render(RoomsController.getRoom(request, response)));

        post("/rooms/:id", (request, response) ->
                render(RoomsController.saveEditedRoom(request, response)));

        get("/rooms/:id/edit", (request, response) ->
                render(RoomsController.editRoom(request, response)));

        post("/rooms/:id/edit/users", (request, response) ->
                render(RoomsController.addUser(request, response)));

        post("/rooms/:id/edit/users/delete", (request, response) ->
                render(RoomsController.removeUser(request, response)));

        post("/rooms/:id/delete", (request, response) ->
                render(RoomsController.deleteRoom(request, response)));


        //***************  MODULES  ***************
        get("/modules", (request, response) ->
                render(ModulesController.getModules(request, response)));

        post("/modules", (request, response) ->
                render(ModulesController.saveModule(request, response)));

        post("/modules/:id/delete", (request, response) ->
                render(ModulesController.deleteModule(request, response)));

        get("/modules/new", (request, response) ->
                render(ModulesController.newModule(request, response)));

        get("/modules/:id", (request, response) ->
                render(ModulesController.getModule(request, response)));

        get("/modules/:id/edit", (request, response) ->
                render(ModulesController.editModule(request, response)));

        post("/modules/:id", (request, response) ->
                render(ModulesController.saveEditedModule(request, response)));

        //***************  ERRORS  ***************
        notFound((request, response) ->
                render(SmartDomController.getNotFound(request, response)));

        internalServerError((request, response) ->
                render(SmartDomController.getServerError(request, response)));


        final Config config = new SmartDomConfigFactory(new VelocityTemplateEngine()).build();

        before(new SecurityFilter(config, "CookieClient", "custom", "excludedMain,excludedRegister,excludedApi, excludedSignIn"));
        before("/api/*", new SecurityFilter(config, "HeaderClient", "custom", "excludedLogin"));
    }

    private static String render(ModelAndView modelAndView) {
        if (modelAndView != null)
            return new VelocityTemplateEngine().render(modelAndView);
        else
            return null;
    }

}
