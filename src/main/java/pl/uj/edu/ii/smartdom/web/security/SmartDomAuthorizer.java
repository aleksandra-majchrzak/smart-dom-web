package pl.uj.edu.ii.smartdom.web.security;

import org.bson.types.ObjectId;
import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.User;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mohru on 30.08.2017.
 */
public class SmartDomAuthorizer extends ProfileAuthorizer<CommonProfile> {
    @Override
    protected boolean isProfileAuthorized(WebContext context, CommonProfile profile) throws HttpAction {
        if (profile == null) {
            return false;
        }

        User user = DatabaseManager.getDataStore().get(User.class, new ObjectId(profile.getUsername()));
        context.setRequestAttribute("isAdmin", user.isAdmin());

        String path = context.getPath();
        Pattern p = Pattern.compile("/users[/.*]?");
        Matcher m = p.matcher(path);
        if (m.find()) {
            return user.isAdmin();
        }
        // todo sprawdzic czy dant uzytkownik ma uprawnienie do danego zasobu np. modulu/pokoju/strony z userami
        return true;
    }

    @Override
    public boolean isAuthorized(WebContext context, List<CommonProfile> profiles) throws HttpAction {
        return isAnyAuthorized(context, profiles);
    }
}
