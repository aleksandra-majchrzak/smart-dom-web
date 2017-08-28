package pl.uj.edu.ii.smartdom.web.serverEntities;

/**
 * Created by Mohru on 15.07.2017.
 */
public class Door extends Module {
    public boolean isOpen;

    public Door(String serverId, boolean isOpen) {
        super(serverId);
        this.isOpen = isOpen;
    }
}