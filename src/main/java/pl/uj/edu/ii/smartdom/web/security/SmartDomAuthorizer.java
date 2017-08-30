package pl.uj.edu.ii.smartdom.web.security;

import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

/**
 * Created by Mohru on 30.08.2017.
 */
public class SmartDomAuthorizer extends ProfileAuthorizer<CommonProfile> {
    @Override
    protected boolean isProfileAuthorized(WebContext context, CommonProfile profile) throws HttpAction {
        if (profile == null) {
            return false;
        }

        String path = context.getPath();
        // todo sprawdzic czy dant uzytkownik ma uprawnienie do danego zasobu np. modulu/pokoju/strony z userami
        return true;
    }

    @Override
    public boolean isAuthorized(WebContext context, List<CommonProfile> profiles) throws HttpAction {
        return isAnyAuthorized(context, profiles);
    }
}
