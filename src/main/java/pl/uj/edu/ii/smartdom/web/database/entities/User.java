package pl.uj.edu.ii.smartdom.web.database.entities;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohru on 15.07.2017.
 */
@Entity("users")
public class User {
    @Id
    private ObjectId id;
    private String login;
    private String password;
    @Reference
    private List<Room> rooms = new ArrayList<>();
    private boolean isConfirmed;
    private boolean isAdmin;

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.isConfirmed = false;
        this.isAdmin = false;
    }

    public User(String login, String password, boolean isConfirmed, boolean isAdmin) {
        this.login = login;
        this.password = password;
        this.isConfirmed = isConfirmed;
        this.isAdmin = isAdmin;
    }

    public User(String login, String password, List<Room> rooms) {
        this.login = login;
        this.password = password;
        this.rooms = rooms;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
