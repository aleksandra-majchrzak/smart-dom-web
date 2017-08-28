package pl.uj.edu.ii.smartdom.web.serverEntities;

/**
 * Created by Mohru on 15.07.2017.
 */
public class MeteoResponse {

    public double temperature;
    public double humidity;
    public double co;
    public double co2;
    public double gas;

    public MeteoResponse() {
    }

    public MeteoResponse(double temperature, double humidity, double co, double co2, double gas) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.co = co;
        this.co2 = co2;
        this.gas = gas;
    }
}
