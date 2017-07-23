package controllers;

import database.DatabaseManager;
import database.entities.User;
import org.mongodb.morphia.query.Query;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohru on 23.07.2017.
 */
public class UserController {

    public static ModelAndView signIn(Request request, Response response) {
        Map<String, Object> model = new HashMap<String, Object>();

        String username = request.queryParams("username");
        String password = request.queryParams("password");

        Query<User> userQuery = DatabaseManager.getDataStore().createQuery(User.class)
                .filter("login =", username);

        List<User> users = userQuery.asList();

        if (!users.isEmpty()) {
            User user = users.get(0);
            if (user.getPassword().equals(password)) {
                request.session().attribute("currentUser", user);
                model.put("currentUsername", user.getLogin());

                return new ModelAndView(model, "/public/index.vm");
            } else {
                model.put("error", "Zły login lub hasło");
            }
        } else {
            model.put("error", "Użytkownik nie istnieje");
        }

        return new ModelAndView(model, "/public/index.vm");
    }

    public static ModelAndView register(Request request, Response response) {
        Map<String, Object> model = new HashMap<String, Object>();

        String username = request.queryParams("username");
        String password = request.queryParams("password");
        String passwordConfirm = request.queryParams("passwordConfirm");

        Query<User> userQuery = DatabaseManager.getDataStore().createQuery(User.class)
                .filter("login =", username);

        List<User> users = userQuery.asList();

        if (users.isEmpty()) {
            if (passwordConfirm.equals(password)) {
                User user = new User(username, password);
                DatabaseManager.getDataStore().save(user);
                request.session().attribute("currentUser", user);
                model.put("currentUsername", user.getLogin());

                return new ModelAndView(model, "/public/register.vm");
            } else {
                model.put("error", "Hasło i potwierdzenie nie są takie same");
            }
        } else {
            model.put("error", "Użytkownik o podanym loginie już istnieje");
        }

        return new ModelAndView(model, "/public/register.vm");
    }

    public static ModelAndView logOut(Request request, Response response) {
        request.session(false).invalidate();
        response.redirect("/");
        return null;
    }
}
