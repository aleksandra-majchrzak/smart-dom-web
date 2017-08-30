package pl.uj.edu.ii.smartdom.web.security;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.User;

/**
 * Created by Mohru on 30.08.2017.
 */
public class SmartDomLoginPwdAuthenticator implements Authenticator<UsernamePasswordCredentials> {
    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction, CredentialsException {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        if (CommonHelper.isBlank(username)) {
            throwsException("Login nie może być pusty.");
        }
        if (CommonHelper.isBlank(password)) {
            throwsException("Hasło nie może być puste.");
        }

        User user = DatabaseManager.getDataStore().find(User.class, "name", username).get();

        if (user == null) {
            throwsException("Błędny login lub hasło.");
        } else if (user.getPassword().equals(password)) {
            final CommonProfile profile = new CommonProfile();
            profile.setId(username);
            profile.addAttribute(Pac4jConstants.USERNAME, username);
            credentials.setUserProfile(profile);
        } else {
            throwsException("Błędny login lub hasło.");
        }

    }

    protected void throwsException(final String message) throws CredentialsException {
        throw new CredentialsException(message);
    }
}
