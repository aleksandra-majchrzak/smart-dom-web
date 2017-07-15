import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

/**
 * Created by Mohru on 15.07.2017.
 */
public class WebApp {

    public static void initialize() {

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
    }
}
