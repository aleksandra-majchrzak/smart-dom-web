package pl.uj.edu.ii.smartdom.web.serverEntities;

import java.util.Map;

/**
 * Created by Mohru on 08.08.2017.
 */

public class Light extends Module {
    private Map<String, Integer> rgb;
    private int brightness;

    public Light(String serverId, Map<String, Integer> rgb) {
        super(serverId);
        this.rgb = rgb;
    }

    public Light(String serverId, int brightness) {
        super(serverId);
        this.brightness = brightness;
    }

    public Map<String, Integer> getRgb() {
        return rgb;
    }

    public int getBrightness() {
        return brightness;
    }
}
