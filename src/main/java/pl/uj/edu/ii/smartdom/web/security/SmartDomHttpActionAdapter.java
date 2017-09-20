package pl.uj.edu.ii.smartdom.web.security;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;
import org.pac4j.sparkjava.SparkWebContext;
import pl.uj.edu.ii.smartdom.web.controllers.SmartDomController;
import spark.ModelAndView;
import spark.TemplateEngine;

import static spark.Spark.halt;

/**
 * Created by Mohru on 30.08.2017.
 */
public class SmartDomHttpActionAdapter extends DefaultHttpActionAdapter {

    private final TemplateEngine templateEngine;

    public SmartDomHttpActionAdapter(final TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public Object adapt(int code, SparkWebContext context) {
        if (code == HttpConstants.UNAUTHORIZED || code == HttpConstants.FORBIDDEN) {
            ModelAndView modelAndView = SmartDomController.getMain(context.getSparkRequest(), context.getSparkResponse());
            if (modelAndView != null)
                halt(code, templateEngine.render(modelAndView));
        } else {
            return super.adapt(code, context);
        }
        return null;
    }
}
