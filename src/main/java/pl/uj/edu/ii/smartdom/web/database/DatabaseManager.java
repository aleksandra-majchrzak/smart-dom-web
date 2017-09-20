package pl.uj.edu.ii.smartdom.web.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import pl.uj.edu.ii.smartdom.web.database.entities.Module;
import pl.uj.edu.ii.smartdom.web.database.entities.Room;
import pl.uj.edu.ii.smartdom.web.database.entities.User;
import pl.uj.edu.ii.smartdom.web.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohru on 15.07.2017.
 */
public class DatabaseManager {

    private static final String HOST = "localhost";
    private static final int PORT = 27017;
    private static final String AUTH_DB_NAME = "admin";
    private static final String DB_NAME = "smart_dom";
    private static final String USERNAME = "Admin";
    private static final String PSWD = "MySmartPassword123";


    private static Morphia morphia = new Morphia();
    private static Datastore datastore = null;

    private DatabaseManager() {
    }

    public static void init() {
        if (!morphia.isMapped(User.class)) {
            morphia.map(User.class);
            morphia.map(Room.class);
            morphia.map(Module.class);
            initDatastore();

            if (datastore.getCount(User.class) == 0) {
                User user = new User("Admin", StringUtils.getHashString("ChangeThisPassword"), true, true);
                datastore.save(user);
            }

        } else {
            System.out.println("Database Class Mapped Already!");
        }
    }

    public static Datastore getDataStore() {
        if (datastore == null) {
            initDatastore();
        }

        return datastore;
    }

    static void initDatastore() {

        MongoClient mongoClient;

        try {
            ServerAddress address = new ServerAddress(HOST, PORT);
            List<MongoCredential> credentials = new ArrayList<>();
            MongoCredential credential = MongoCredential.createCredential(USERNAME, AUTH_DB_NAME, PSWD.toCharArray());
            credentials.add(credential);
            mongoClient = new MongoClient(address, credentials);
            datastore = morphia.createDatastore(mongoClient, DB_NAME);


            datastore.ensureIndexes();
            System.out.println("Database connection successful and Datastore initiated");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }


}
