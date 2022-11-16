package room;

import java.util.LinkedHashSet;
import level.elements.tile.DoorTile;

public interface IRoom {
    /**
     * @return all DoorTiles in this room
     */
    LinkedHashSet<DoorTile> getDoors();

    /** remove the ExitTile in this room if exist */
    void removeExit();
}
