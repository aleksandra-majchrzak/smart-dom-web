package pl.uj.edu.ii.smartdom.web.enums;

/**
 * Created by Mohru on 23.07.2017.
 */
public enum ModuleType {
    LIGHT_MODULE("LIGHT_MODULE"), BLIND_MOTOR_MODULE("BLIND_MOTOR_MODULE"), METEO_MODULE("METEO_MODULE");

    private String type;

    private ModuleType(String type) {
        this.type = type;
    }

    public final String getValue() {
        return type;
    }

    public String getName() {
        switch (this) {
            case LIGHT_MODULE:
                return "Swiatło";
            case BLIND_MOTOR_MODULE:
                return "Roleta";
            case METEO_MODULE:
                return "Meteo";
            default:
                return "Swiatło";
        }
    }
}