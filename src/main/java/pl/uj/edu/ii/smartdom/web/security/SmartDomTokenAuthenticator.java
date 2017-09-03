package pl.uj.edu.ii.smartdom.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.User;
import pl.uj.edu.ii.smartdom.web.utils.JwtUtils;

/**
 * Created by Mohru on 03.09.2017.
 */
public class SmartDomTokenAuthenticator implements Authenticator<TokenCredentials> {
    @Override
    public void validate(final TokenCredentials credentials, final WebContext context) throws HttpAction, CredentialsException {
        String token = credentials.getToken();
        String clientName = credentials.getClientName();

        if (CommonHelper.isBlank(token)) {
            throwsException("Token nie może być pusty.");
        }
        if (CommonHelper.isBlank(clientName)) {
            throwsException("Nazwa klienta nie może być puste.");
        }

        try {
            Jws<Claims> claims = JwtUtils.parseJwtToken(token);
            String subject = claims.getBody().getSubject();
            User user = DatabaseManager.getDataStore().find(User.class, "login", subject).get();

            if (user == null) {
                throwsException("Błędny login lub hasło.");
            } else {
                if ((clientName.equals("CookieClient") && subject.equals(context.getSessionAttribute("username")))
                        || (clientName.equals("HeaderClient") && subject.equals(context.getRequestParameter("login")))) {

                    final CommonProfile profile = new CommonProfile();
                    profile.addAttribute(Pac4jConstants.CSRF_TOKEN, token);
                    profile.addAttribute(Pac4jConstants.USERNAME, subject);
                    credentials.setUserProfile(profile);
                } else {
                    throwsException("Nie można zautentykowac klienta.");
                }
            }
        } catch (Exception e) {
            throwsException("Nie można zautentykowac klienta.");
        }
    }

    protected void throwsException(final String message) throws CredentialsException {
        throw new CredentialsException(message);
    }
}
