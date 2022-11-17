package levelgraph;

import java.util.LinkedHashSet;
import level.elements.tile.DoorTile;
import level.elements.tile.Tile;
import level.tools.Coordinate;
import level.tools.DesignLabel;
import level.tools.LevelSize;
import room.IRoom;
import room.RoomGenerator;

/**
 * @author Andre Matutat
 */
public class GraphLevel {
    private LevelNode root;
    private LevelSize size;
    private DesignLabel designLabel;
    private RoomGenerator generator;

    /**
     * @param root Root-Node of the graph
     * @param size The level size
     * @param designLabel The design of the rooms
     */
    public GraphLevel(LevelNode root, LevelSize size, DesignLabel designLabel) {
        this.root = root;
        this.designLabel = designLabel;
        this.size = size;
        generator = new RoomGenerator();
        createRooms(root, new LinkedHashSet());
        findDoors(root, new LinkedHashSet());
    }

    // Visit all Nodes and create a room for each of them
    private void createRooms(LevelNode node, LinkedHashSet<LevelNode> visited) {
        if (node == null || visited.contains(node)) return;
        node.setRoom(generator.getLevel(designLabel, size, node.getNeighboursAsDirection()));
        visited.add(node);
        for (LevelNode neighbour : node.getNeighbours()) createRooms(neighbour, visited);
    }

    // Add the connection between the doors
    private void findDoors(LevelNode node, LinkedHashSet<LevelNode> visited) {
        if (node == null || visited.contains(node)) return;
        visited.add(node);

        LevelNode rightNeighbour = node.getNeighbour(DoorDirection.RIGHT);
        LevelNode leftNeighbour = node.getNeighbour(DoorDirection.LEFT);
        LevelNode lowerNeighbour = node.getNeighbour(DoorDirection.DOWN);
        LevelNode upperNeighbour = node.getNeighbour(DoorDirection.UP);
        DoorTile[] doors = findDoors(node.getRoom());

        if (rightNeighbour != null)
            doorPairFound(
                    doors[DoorDirection.RIGHT.getValue()],
                    findDoors(rightNeighbour.getRoom())[DoorDirection.LEFT.getValue()],
                    DoorDirection.RIGHT,
                node);
        if (leftNeighbour != null)
            doorPairFound(
                    doors[DoorDirection.LEFT.getValue()],
                    findDoors(leftNeighbour.getRoom())[DoorDirection.RIGHT.getValue()],
                    DoorDirection.LEFT,
                node);
        if (lowerNeighbour != null)
            doorPairFound(
                    doors[DoorDirection.DOWN.getValue()],
                    findDoors(lowerNeighbour.getRoom())[DoorDirection.UP.getValue()],
                    DoorDirection.DOWN,
                node);
        if (upperNeighbour != null)
            doorPairFound(
                    doors[DoorDirection.UP.getValue()],
                    findDoors(upperNeighbour.getRoom())[DoorDirection.DOWN.getValue()],
                    DoorDirection.UP,
                    node);

        for (LevelNode child : node.getNeighbours()) findDoors(child, visited);
    }

    private void doorPairFound(
            DoorTile door, DoorTile otherDoor, DoorDirection direction, LevelNode node) {
        door.setOtherDoor(otherDoor);
        door.setColor(node.getColors()[direction.getValue()]);
        findDoorstep(door, direction, node.getRoom());
    }

    private DoorTile[] findDoors(IRoom room) {
        DoorTile[] doorsInOrder = new DoorTile[4];
        for (DoorTile door : room.getDoors()) {
            if (belowIsAccessible(door.getCoordinate(), room.getLayout())) {
                doorsInOrder[DoorDirection.UP.getValue()] = door;
            } else if (leftIsAccessible(door.getCoordinate(), room.getLayout())) {
                doorsInOrder[DoorDirection.RIGHT.getValue()] = door;
            } else if (rightIsAccessible(door.getCoordinate(), room.getLayout())) {
                doorsInOrder[DoorDirection.LEFT.getValue()] = door;
            } else if (aboveIsAccessible(door.getCoordinate(), room.getLayout())) {
                doorsInOrder[DoorDirection.DOWN.getValue()] = door;
            }
        }
        return doorsInOrder;
    }

    private boolean belowIsAccessible(Coordinate c, Tile[][] layout) {
        try {
            return layout[c.y - 1][c.x].isAccessible();

        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private boolean leftIsAccessible(Coordinate c, Tile[][] layout) {
        try {
            return layout[c.y][c.x - 1].isAccessible();

        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private boolean rightIsAccessible(Coordinate c, Tile[][] layout) {
        try {
            return layout[c.y][c.x + 1].isAccessible();

        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private boolean aboveIsAccessible(Coordinate c, Tile[][] layout) {
        try {
            return layout[c.y + 1][c.x].isAccessible();

        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }



    private void findDoorstep(DoorTile door, DoorDirection direction, IRoom room) {
        Tile doorstep = null;
        Coordinate doorCoordinate = door.getCoordinate();
        Tile[][] layout = room.getLayout();
        switch (direction) {
            case UP:
                doorstep = layout[doorCoordinate.y - 1][doorCoordinate.x];
                break;
            case RIGHT:
                doorstep = layout[doorCoordinate.y][doorCoordinate.x - 1];
                break;
            case LEFT:
                doorstep = layout[doorCoordinate.y][doorCoordinate.x + 1];
                break;
            case DOWN:
                doorstep = layout[doorCoordinate.y + 1][doorCoordinate.x];
                break;
        }
        if (doorstep == null) throw new NullPointerException("DoorStep not found");
        door.setDoorstep(doorstep);
    }

    /**
     * @return The Room that is saved in the root-Node
     */
    public IRoom getRootRoom() {
        return root.getRoom();
    }
}
