package pl.uj.edu.ii.smartdom.web.utils;

/**
 * Created by Mohru on 02.09.2017.
 */
public class JmDNSService {
    private String name;
    private String address;
    private int port;
    private boolean isConnected;

    public JmDNSService(String name, String address, int port, boolean isConnected) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.isConnected = isConnected;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
