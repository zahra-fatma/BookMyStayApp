import java.util.HashMap;
import java.util.Map;
/* Abstract Room Class */
abstract class Room {

    protected int beds;
    protected int size;
    protected double pricePerNight;

    public Room(int beds, int size, double pricePerNight) {
        this.beds = beds;
        this.size = size;
        this.pricePerNight = pricePerNight;
    }

    public void displayDetails() {
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sqft");
        System.out.println("Price per night: " + pricePerNight);
    }
}

/* Concrete Room Classes */

class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 250, 1500.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 400, 2500.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 750, 5000.0);
    }
}

/* Inventory Class (Read-only access used in search) */
class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }
}

/* Search Service */
class RoomSearchService {

    private RoomInventory inventory;
    private HashMap<String, Room> roomCatalog;

    public RoomSearchService(RoomInventory inventory, HashMap<String, Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    public void displayAvailableRooms() {

        System.out.println("Hotel Room Inventory Status\n");

        for (String roomType : roomCatalog.keySet()) {

            int available = inventory.getAvailability(roomType);

            // Filter unavailable rooms
            if (available > 0) {

                System.out.println(roomType + " Room:");
                roomCatalog.get(roomType).displayDetails();
                System.out.println("Available Rooms: " + available);
                System.out.println();
            }
        }
    }
}

/* Application Entry Point */
public class UseCase4RoomSearch {

    public static void main(String[] args) {

        // Initialize room objects
        HashMap<String, Room> roomCatalog = new HashMap<>();

        roomCatalog.put("Single", new SingleRoom());
        roomCatalog.put("Double", new DoubleRoom());
        roomCatalog.put("Suite", new SuiteRoom());

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        inventory.addRoomType("Single", 5);
        inventory.addRoomType("Double", 3);
        inventory.addRoomType("Suite", 2);

        // Search service
        RoomSearchService searchService = new RoomSearchService(inventory, roomCatalog);

        // Display available rooms
        searchService.displayAvailableRooms();
    }
}