import java.util.*;

// Custom Exception
class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}

// Reservation Model
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isActive;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isActive = true;
    }

    public String getReservationId() { return reservationId; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public boolean isActive() { return isActive; }

    public void cancel() {
        this.isActive = false;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", RoomType: " + roomType +
                ", RoomID: " + roomId +
                ", Status: " + (isActive ? "CONFIRMED" : "CANCELLED");
    }
}

// Inventory Manager
class RoomInventory {
    private Map<String, Integer> inventory;
    private Map<String, Stack<String>> availableRooms;

    public RoomInventory() {
        inventory = new HashMap<>();
        availableRooms = new HashMap<>();

        // Initialize
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 2);

        availableRooms.put("Standard", new Stack<>());
        availableRooms.put("Deluxe", new Stack<>());

        // Add Room IDs
        availableRooms.get("Standard").push("S1");
        availableRooms.get("Standard").push("S2");

        availableRooms.get("Deluxe").push("D1");
        availableRooms.get("Deluxe").push("D2");
    }

    public String allocateRoom(String roomType) throws BookingException {
        if (!availableRooms.containsKey(roomType)) {
            throw new BookingException("Invalid room type.");
        }

        Stack<String> rooms = availableRooms.get(roomType);

        if (rooms.isEmpty()) {
            throw new BookingException("No rooms available.");
        }

        inventory.put(roomType, inventory.get(roomType) - 1);
        return rooms.pop(); // LIFO allocation
    }

    public void releaseRoom(String roomType, String roomId) {
        availableRooms.get(roomType).push(roomId); // rollback using stack
        inventory.put(roomType, inventory.get(roomType) + 1);
    }

    public void displayInventory() {
        System.out.println("Inventory: " + inventory);
    }
}

// Booking Service
class BookingService {
    private RoomInventory inventory;
    private Map<String, Reservation> reservations;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.reservations = new HashMap<>();
    }

    public Reservation createBooking(String id, String guest, String roomType)
            throws BookingException {

        String roomId = inventory.allocateRoom(roomType);
        Reservation res = new Reservation(id, guest, roomType, roomId);
        reservations.put(id, res);

        return res;
    }

    public Reservation getReservation(String id) {
        return reservations.get(id);
    }
}

// Cancellation Service (Rollback Logic)
class CancellationService {
    private BookingService bookingService;
    private RoomInventory inventory;

    public CancellationService(BookingService bookingService, RoomInventory inventory) {
        this.bookingService = bookingService;
        this.inventory = inventory;
    }

    public void cancelBooking(String reservationId) throws BookingException {

        Reservation res = bookingService.getReservation(reservationId);

        // Validation
        if (res == null) {
            throw new BookingException("Reservation does not exist.");
        }

        if (!res.isActive()) {
            throw new BookingException("Booking already cancelled.");
        }

        // Rollback sequence (controlled mutation)
        String roomType = res.getRoomType();
        String roomId = res.getRoomId();

        // 1. Release room (LIFO rollback)
        inventory.releaseRoom(roomType, roomId);

        // 2. Update booking state
        res.cancel();

        System.out.println("Cancellation successful for: " + reservationId);
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);
        CancellationService cancellationService =
                new CancellationService(bookingService, inventory);

        try {
            // Create bookings
            Reservation r1 = bookingService.createBooking("RES301", "Arun", "Standard");
            Reservation r2 = bookingService.createBooking("RES302", "Priya", "Deluxe");

            System.out.println(r1);
            System.out.println(r2);
            inventory.displayInventory();

            // Cancel booking
            System.out.println("\nCancelling RES301...");
            cancellationService.cancelBooking("RES301");
            inventory.displayInventory();

            // Try invalid cancellation
            System.out.println("\nCancelling RES301 again...");
            cancellationService.cancelBooking("RES301");

        } catch (BookingException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // System continues safely
        System.out.println("\nFinal System State:");
        inventory.displayInventory();
    }
}