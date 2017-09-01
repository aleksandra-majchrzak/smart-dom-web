package pl.uj.edu.ii.smartdom.web.security;

import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.http.client.indirect.FormClient;
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

        final FormClient formClient = new FormClient("https://localhost:4567/", new SmartDomLoginPwdAuthenticator());
        final DirectBasicAuthClient directBasicAuthClient = new DirectBasicAuthClient(new SmartDomLoginPwdAuthenticator());

        final HeaderClient headerClient = new HeaderClient("Authorization", (credentials, ctx) -> {
            final String token = ((TokenCredentials) credentials).getToken();
            //todo sprawdzenie czy token sie zgadza
            if (CommonHelper.isNotBlank(token)) {
                final CommonProfile profile = new CommonProfile();
                profile.setId(token);
                credentials.setUserProfile(profile);
            }
        });

        // todo ten klient tez powinien byc chyba na podstawie tokenu- dlaczego profil uzytkownika sie nie zapisuje w sesji?
        final Clients clients = new Clients("http://localhost:4567/", formClient, headerClient, directBasicAuthClient);

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
