package pl.uj.edu.ii.smartdom.web.controllers;

import pl.uj.edu.ii.smartdom.web.database.entities.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohru on 28.08.2017.
 */
public class SmartDomController {

    public static ModelAndView getMain(Request request, Response response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = request.session().attribute("currentUser");

        if (user != null) {
            response.redirect("/menu");
            return null;
        }

        return new ModelAndView(model, "/public/index.vm");
    }

    public static ModelAndView getRegister(Request request, Response response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = request.session().attribute("currentUser");

        if (user != null) {
            response.redirect("/menu");
            return null;
        }

        return new ModelAndView(model, "/public/register.vm");
    }

    public static ModelAndView getMenu(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView(model, "/public/menu.vm");
    }

    public static ModelAndView getNotFound(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView(model, "/public/notFound.vm");
    }

    public static ModelAndView getServerError(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView(model, "/public/serverError.vm");
    }
}
