import java.util.*;

// Reservation class representing a booking request
class Reservation {
    String guestName;
    String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

public class UseCase6RoomAllocationService {

    public static void main(String[] args) {

        // Queue storing booking requests (FIFO)
        Queue<Reservation> bookingQueue = new LinkedList<>();

        bookingQueue.add(new Reservation("ABHI", "SINGLE"));
        bookingQueue.add(new Reservation("SUBHA", "DOUBLE"));
        bookingQueue.add(new Reservation("RAHUL", "SINGLE"));

        // Inventory of rooms
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("SINGLE", 2);
        inventory.put("DOUBLE", 1);

        // Track allocated room IDs
        Map<String, Set<String>> allocatedRooms = new HashMap<>();

        System.out.println("Room allocation processing");

        while (!bookingQueue.isEmpty()) {

            Reservation request = bookingQueue.poll();
            String roomType = request.roomType;

            // Check availability
            if (inventory.getOrDefault(roomType, 0) > 0) {

                // Generate room ID
                int roomNumber = allocatedRooms
                        .getOrDefault(roomType, new HashSet<>())
                        .size() + 1;

                String roomID = roomType + "-" + roomNumber;

                // Store allocated room IDs
                allocatedRooms
                        .computeIfAbsent(roomType, k -> new HashSet<>())
                        .add(roomID);

                // Update inventory
                inventory.put(roomType, inventory.get(roomType) - 1);

                // Confirm reservation
                System.out.println("Booking confirm for Guest : "
                        + request.guestName
                        + " , Room ID: "
                        + roomID);

            } else {

                System.out.println("No rooms available for Guest : "
                        + request.guestName
                        + " , Requested Type: "
                        + roomType);
            }
        }
    }
}