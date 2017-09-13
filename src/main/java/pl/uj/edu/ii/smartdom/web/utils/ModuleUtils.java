package pl.uj.edu.ii.smartdom.web.utils;

import pl.uj.edu.ii.smartdom.web.database.entities.Module;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Mohru on 02.09.2017.
 */
public class ModuleUtils {

    public static HttpURLConnection getPostConnection(Module module, String method) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(getModuleRequestURL(module, method)).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static HttpURLConnection getGetConnection(Module module, String method) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(getModuleRequestURL(module, method)).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static String getModuleRequestURL(Module module, String method) {
        return "http://" + module.getAddress() + ":" + module.getPort() + "/" + method;
    }
}
