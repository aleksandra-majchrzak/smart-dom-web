package pl.uj.edu.ii.smartdom.web.security;

import org.bson.types.ObjectId;
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
            String subject = JwtUtils.getUserIdFromToken(token);
            User user = DatabaseManager.getDataStore().get(User.class, new ObjectId(subject));

            if (user == null) {
                throwsException("Błędny login lub hasło.");
            } else {
                if ((clientName.equals("CookieClient") && user.getLogin().equals(context.getSessionAttribute("username")))
                        || (clientName.equals("HeaderClient") && user.getLogin().equals(context.getRequestParameter("login")))) {

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
