package pl.uj.edu.ii.smartdom.web.database.entities;

import pl.uj.edu.ii.smartdom.web.enums.ModuleType;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by Mohru on 15.07.2017.
 */
@Entity("modules")
public abstract class Module {
    @Id
    private ObjectId id;
    private String name;
    private ModuleType type;
    private int port;

    public Module(String name) {
        this.name = name;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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
}
