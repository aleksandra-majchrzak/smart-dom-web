package pl.uj.edu.ii.smartdom.web.security;

import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.http.client.direct.CookieClient;
import org.pac4j.http.client.direct.HeaderClient;
import spark.TemplateEngine;

/**
 * Created by Mohru on 30.08.2017.
 */
public class SmartDomConfigFactory {

    private TemplateEngine templateEngine;

    public SmartDomConfigFactory(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public Config build() {

        final CookieClient cookieClient = new CookieClient("auth_token", new SmartDomTokenAuthenticator());
        final HeaderClient headerClient = new HeaderClient("Authorization", new SmartDomTokenAuthenticator());

        final Clients clients = new Clients("https://localhost:4567/", headerClient, cookieClient);

        final Config config = new Config(clients);
        config.addAuthorizer("custom", new SmartDomAuthorizer());
        config.addMatcher("excludedMain", new PathMatcher().excludeRegex("^/$"));
        config.addMatcher("excludedSignIn", new PathMatcher().excludeBranch("/signIn"));
        config.addMatcher("excludedRegister", new PathMatcher().excludeBranch("/register"));
        config.addMatcher("excludedApi", new PathMatcher().excludeBranch("/api"));
        config.addMatcher("excludedLogin", new PathMatcher().excludeBranch("/api/login"));
        config.setHttpActionAdapter(new SmartDomHttpActionAdapter(templateEngine));
        return config;
    }
}
