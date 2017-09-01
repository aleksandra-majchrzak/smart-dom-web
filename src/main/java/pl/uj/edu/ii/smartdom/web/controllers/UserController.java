package pl.uj.edu.ii.smartdom.web.controllers;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mongodb.morphia.query.Query;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;

/**
 * Created by Mohru on 23.07.2017.
 */
public class UserController {

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
            if (user.getPassword().equals(password)) {

                String token = createJWT(UUID.randomUUID().toString(), "smart_dom", user.getLogin());
                response.cookie("auth_token", token);
                request.session().attribute("username", user.getLogin());
                model.put("currentUsername", user.getLogin());

                response.redirect("/menu");
                return null;
            } else {
                model.put("errors", Collections.singletonList("Zły login lub hasło"));
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
                User user = new User(username, password);
                DatabaseManager.getDataStore().save(user);
                request.session().attribute("username", user.getLogin());
                model.put("currentUsername", user.getLogin());

                return new ModelAndView(model, "/public/register.vm");
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
        response.redirect("/");
        return null;
    }

    private static String createJWT(String id, String issuer, String subject) {
//The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("someSecret");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        /*if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }*/

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }
}
