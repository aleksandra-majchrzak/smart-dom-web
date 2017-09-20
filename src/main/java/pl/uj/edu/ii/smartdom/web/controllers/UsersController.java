package pl.uj.edu.ii.smartdom.web.controllers;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.Room;
import pl.uj.edu.ii.smartdom.web.database.entities.User;
import pl.uj.edu.ii.smartdom.web.utils.JwtUtils;
import pl.uj.edu.ii.smartdom.web.utils.StringUtils;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;

/**
 * Created by Mohru on 23.07.2017.
 */
public class UsersController {

    byte[] bytes = {0, 1, 2};
    private Key apiKey = new SecretKeySpec(bytes, "");

    public static ModelAndView signIn(Request request, Response response) {
        Map<String, Object> model = new HashMap<String, Object>();

        String username = request.queryParams("username");
        String password = request.queryParams("password");

        Query<User> userQuery = DatabaseManager.getDataStore().createQuery(User.class)
                .filter("login =", username);

        List<User> users = userQuery.asList();

        if (!users.isEmpty()) {
            User user = users.get(0);
            if (user.isConfirmed()) {
                if (user.getPassword().equals(StringUtils.getHashString(password))) {

                    String token = JwtUtils.createJWT(UUID.randomUUID().toString(), "smart_dom", user.getId().toHexString());
                    response.cookie("auth_token", token, 600, true, true);

                    request.session().attribute("username", user.getLogin());
                    response.redirect("/menu");
                    return null;
                } else {
                    model.put("errors", Collections.singletonList("Zły login lub hasło"));
                }
            } else {
                model.put("errors", Collections.singletonList("Użytkownik nie został jeszcze zatwierdzony przez admina."));
            }
        } else {
            model.put("errors", Collections.singletonList("Użytkownik nie istnieje"));
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
                User user = new User(username, StringUtils.getHashString(password));
                DatabaseManager.getDataStore().save(user);

                request.session().attribute("info", "Twoje konto zostało zarejestrowane. Poczekaj na akceptację admina.");
                response.redirect("/");
                return null;
            } else {
                model.put("errors", Collections.singletonList("Hasło i potwierdzenie nie są takie same"));
            }
        } else {
            model.put("errors", Collections.singletonList("Użytkownik o podanym loginie już istnieje"));
        }

        return new ModelAndView(model, "/public/register.vm");
    }

    public static ModelAndView logOut(Request request, Response response) {
        request.session(false).invalidate();
        request.session().attribute("username", null);
        response.removeCookie("auth_token");
        response.redirect("/");
        return null;
    }

    public static ModelAndView getUsers(Request request, Response response) {
        List<User> users = DatabaseManager.getDataStore().find(User.class).filter("isAdmin ==", false).asList();

        Map<String, Object> model = new HashMap<>();
        model.put("panelName", "Users");
        model.put("users", users);
        model.put("username", request.session().attribute("username"));
        model.put("isAdmin", request.attribute("isAdmin"));

        String info = request.session().attribute("info");
        if (info != null) {
            model.put("info", info);
            request.session().removeAttribute("info");
        }

        return new ModelAndView(model, "/public/users/users.vm");
    }

    public static ModelAndView activateUser(Request request, Response response) {
        String id = request.params(":id");
        User user = DatabaseManager.getDataStore().get(User.class, new ObjectId(id));
        if (user != null) {
            user.setConfirmed(true);
            DatabaseManager.getDataStore().save(user);
            request.session().attribute("info", "Konto użytkownika " + user.getLogin() + " zostało aktywowane.");
        }

        response.redirect("/users");
        return null;
    }

    public static ModelAndView deleteUser(Request request, Response response) {
        String id = request.params(":id");
        User userToDelete = DatabaseManager.getDataStore().get(User.class, new ObjectId(id));
        if (userToDelete != null) {
            DatabaseManager.getDataStore().delete(userToDelete);
            if (userToDelete.getRooms() != null) {
                Datastore ds = DatabaseManager.getDataStore();
                ds.update(ds.find(Room.class), ds.createUpdateOperations(Room.class).removeAll("userIds", userToDelete.getId()));
                request.session().attribute("info", "Użytkownik usunięty.");
            }
        }

        response.redirect("/users");
        return null;
    }
}
