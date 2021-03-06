package pl.uj.edu.ii.smartdom.web.serverEntities;

import pl.uj.edu.ii.smartdom.web.database.entities.Room;
import pl.uj.edu.ii.smartdom.web.enums.ConnectionType;
import pl.uj.edu.ii.smartdom.web.enums.ModuleType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohru on 23.07.2017.
 */
public class RoomResponse {

    private String id;
    private String name;
    private List<ModuleResponse> modules = new ArrayList<>();

    public RoomResponse(Room room) {
        this.id = room.getId().toHexString();
        this.name = room.getName();
        room.getModules().forEach(mod -> modules.add(new ModuleResponse(mod.getId().toHexString(), mod.getName(),
                mod.getType(), mod.getConnectionType(), mod.getConnectionType() == ConnectionType.BLE ? mod.getAddress() : null)));
    }

    class ModuleResponse {
        private String id;
        private String name;
        private ModuleType type;
        private ConnectionType connectionType;
        private String address;

        public ModuleResponse(String id, String name, ModuleType type, ConnectionType connectionType, String address) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.connectionType = connectionType;
            this.address = address;
        }
    }
}
