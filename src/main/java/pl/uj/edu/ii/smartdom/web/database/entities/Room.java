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
@Entity("rooms")
public class Room {
    @Id
    private ObjectId id;

    private String name;
    @Reference
    private List<Module> modules = new ArrayList<>();
    private List<ObjectId> userIds = new ArrayList<>();

    public Room() {
    }

    public Room(String name) {
        this.name = name;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public List<ObjectId> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<ObjectId> userIds) {
        this.userIds = userIds;
    }
}
