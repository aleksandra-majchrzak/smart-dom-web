import database.DatabaseManager;

/**
 * Created by Mohru on 09.07.2017.
 */
public class Main {

    public static void main(String[] args) {

        DatabaseManager.init();
        WebApp.initialize();
        MobileAPI.initialize();
    }
}