package pl.uj.edu.ii.smartdom.web.serverEntities;

import pl.uj.edu.ii.smartdom.web.enums.ConnectionType;

/**
 * Created by Mohru on 08.08.2017.
 */

public abstract class Module {
    protected String serverId;
    protected ConnectionType connectionType;
    protected String address;

    public Module(String serverId, ConnectionType connectionType, String address) {
        this.serverId = serverId;
        this.connectionType = connectionType;
        this.address = address;
    }

    public Module(String serverId) {
        this.serverId = serverId;
    }

    public String getServerId() {
        return serverId;
    }
}
