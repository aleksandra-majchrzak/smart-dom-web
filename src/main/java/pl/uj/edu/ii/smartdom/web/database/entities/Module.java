package pl.uj.edu.ii.smartdom.web.database.entities;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import pl.uj.edu.ii.smartdom.web.enums.ModuleType;

/**
 * Created by Mohru on 15.07.2017.
 */
@Entity("modules")
public class Module {
    @Id
    private ObjectId id;
    private String name;
    private ModuleType type;
    private int port;
    private String address;
    @Reference
    private Room room;

    public Module() {
    }

    public Module(String name) {
        this.name = name;
    }

    public Module(String name, ModuleType type, String address, int port) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.port = port;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public ModuleType getType() {
        return type;
    }

    public void setType(ModuleType type) {
        this.type = type;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
