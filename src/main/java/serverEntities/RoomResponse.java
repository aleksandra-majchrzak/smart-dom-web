package serverEntities;

import database.entities.Room;
import enums.ModuleType;

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
        room.getModules().forEach(mod -> modules.add(new ModuleResponse(mod.getId().toHexString(), mod.getName(), mod.getType())));
    }

    class ModuleResponse {
        private String id;
        private String name;
        private ModuleType type;

        public ModuleResponse(String id, String name, ModuleType type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }
    }
}
