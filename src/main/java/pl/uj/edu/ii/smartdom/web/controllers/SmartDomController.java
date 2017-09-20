package pl.uj.edu.ii.smartdom.web.controllers;

import org.pac4j.core.context.HttpConstants;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohru on 28.08.2017.
 */
public class SmartDomController {

    public static ModelAndView getMain(Request request, Response response) {
        Map<String, Object> model = new HashMap<String, Object>();
        String username = request.session().attribute("username");

        int statusCode = response.status();
        switch (statusCode) {
            case HttpConstants.UNAUTHORIZED:
                model.put("errors", Collections.singleton("Musisz być zalogowany by oglądać tę stronę."));
                break;
            case HttpConstants.FORBIDDEN:
                model.put("errors", Collections.singleton("Nie masz wystarczających uprawnień by oglądać tę stronę."));
        }

        String info = request.session().attribute("info");
        if (info != null) {
            model.put("info", info);
            request.session().removeAttribute("info");
        }

        if (username != null) {
            response.redirect("/menu");
            return null;
        }

        return new ModelAndView(model, "/public/index.vm");
    }

    public static ModelAndView getRegister(Request request, Response response) {
        Map<String, Object> model = new HashMap<String, Object>();
        String username = request.session().attribute("username");

        if (username != null) {
            response.redirect("/menu");
            return null;
        }

        return new ModelAndView(model, "/public/register.vm");
    }

    public static ModelAndView getMenu(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        model.put("username", request.session().attribute("username"));
        model.put("isAdmin", request.attribute("isAdmin"));

        return new ModelAndView(model, "/public/menu.vm");
    }

    public static ModelAndView getNotFound(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        model.put("username", request.session().attribute("username"));
        return new ModelAndView(model, "/public/notFound.vm");
    }

    public static ModelAndView getServerError(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        model.put("username", request.session().attribute("username"));
        return new ModelAndView(model, "/public/serverError.vm");
    }
}
