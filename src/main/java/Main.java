import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created by Mohru on 09.07.2017.
 */
public class Main {

    public static void main(String[] args) {
        // default port 4567
        staticFileLocation("/public");

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

        post("/signIn", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            System.out.println(req.body());

            System.out.println("username: " + username);
            System.out.println("password: " + password);

            return true;
        });
    }
}