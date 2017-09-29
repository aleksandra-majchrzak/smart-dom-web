package pl.uj.edu.ii.smartdom.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.bson.types.ObjectId;
import org.eclipse.jetty.util.StringUtil;
import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import pl.uj.edu.ii.smartdom.web.Constants;
import pl.uj.edu.ii.smartdom.web.database.DatabaseManager;
import pl.uj.edu.ii.smartdom.web.database.entities.Module;
import pl.uj.edu.ii.smartdom.web.database.entities.User;
import pl.uj.edu.ii.smartdom.web.utils.JwtUtils;

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

        if (user.isAdmin()) {
            context.setRequestAttribute("userId", user.getId().toString());
            return true;
        }

        String path = context.getPath();
        Matcher mUsers = Pattern.compile("/users(/(.*)?)?").matcher(path);
        Matcher mRooms = Pattern.compile("/rooms(/(.*)?)?").matcher(path);
        Matcher mModules = Pattern.compile("/modules(/(.*)?)?").matcher(path);

        String token = (String) profile.getAttribute(Constants.JWT_TOKEN);
        Jws<Claims> claims = JwtUtils.parseJwtToken(token);

        if (mUsers.find()) {
            return false;
        } else if (mRooms.find()) {
            String query = mRooms.group(2);
            if (!StringUtil.isBlank(query)) {
                String scope = claims.getBody().get("scope", String.class);
                return scope.contains(query);
            }
            return true;
        } else if (mModules.find()) {
            String query = mModules.group(2);
            if (!StringUtil.isBlank(query)) {
                String scope = claims.getBody().get("scope", String.class);
                try {
                    Module module = DatabaseManager.getDataStore().get(Module.class, new ObjectId(query));
                    return module != null && module.getRoom() != null
                            && scope.contains(module.getRoom().getId().toHexString());
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean isAuthorized(WebContext context, List<CommonProfile> profiles) throws HttpAction {
        return isAnyAuthorized(context, profiles);
    }
}
