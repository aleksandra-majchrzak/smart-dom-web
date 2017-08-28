package pl.uj.edu.ii.smartdom.web.serverEntities;

/**
 * Created by Mohru on 08.08.2017.
 */

public abstract class Module {
    protected String serverId;

    public Module(String serverId) {
        this.serverId = serverId;
    }

    public String getServerId() {
        return serverId;
    }
}
