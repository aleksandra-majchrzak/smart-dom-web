package serverEntities;

/**
 * Created by Mohru on 27.08.2017.
 */
public class Blind extends Module {

    private boolean shouldStart;

    public Blind(String serverId, boolean shouldStart) {
        super(serverId);
        this.shouldStart = shouldStart;
    }

    public boolean isShouldStart() {
        return shouldStart;
    }
}
