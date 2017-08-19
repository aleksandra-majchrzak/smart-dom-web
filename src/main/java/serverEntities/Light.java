package serverEntities;

import java.util.Map;

/**
 * Created by Mohru on 08.08.2017.
 */

public class Light extends Module {
    private Map<String, Integer> rgb;

    public Light(String serverId, Map<String, Integer> rgb) {
        super(serverId);
        this.rgb = rgb;
    }

    public Map<String, Integer> getRgb() {
        return rgb;
    }
}
