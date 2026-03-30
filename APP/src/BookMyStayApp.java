import java.util.*;

// Custom Exception for Invalid Booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation Model
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private int nights;

    public Reservation(String reservationId, String guestName, String roomType, int nights) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room: " + roomType +
                ", Nights: " + nights;
    }
}

// Inventory Manager (Guarding System State)
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 2);
        inventory.put("Suite", 1);
    }

    public boolean isRoomTypeValid(String roomType) {
        return inventory.containsKey(roomType);
    }

    public int getAvailableRooms(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void bookRoom(String roomType) throws InvalidBookingException {
        int available = getAvailableRooms(roomType);

        if (available <= 0) {
            throw new InvalidBookingException("No available rooms for type: " + roomType);
        }

        inventory.put(roomType, available - 1);
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + inventory);
    }
}

// Validator (Fail-Fast)
class BookingValidator {

    public void validate(String guestName, String roomType, int nights, RoomInventory inventory)
            throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (!inventory.isRoomTypeValid(roomType)) {
            throw new InvalidBookingException("Invalid room type selected.");
        }

        if (nights <= 0) {
            throw new InvalidBookingException("Number of nights must be greater than 0.");
        }

        if (inventory.getAvailableRooms(roomType) <= 0) {
            throw new InvalidBookingException("Selected room type is fully booked.");
        }
    }
}

// Booking Service
class BookingService {
    private RoomInventory inventory;
    private BookingValidator validator;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.validator = new BookingValidator();
    }

    public Reservation createBooking(String reservationId, String guestName,
                                     String roomType, int nights)
            throws InvalidBookingException {

        // Validate first (Fail-Fast)
        validator.validate(guestName, roomType, nights, inventory);

        // Only update state AFTER validation
        inventory.bookRoom(roomType);

        return new Reservation(reservationId, guestName, roomType, nights);
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        // Test cases (Valid + Invalid)
        String[][] testCases = {
                {"RES201", "Arun", "Deluxe", "2"},
                {"RES202", "", "Suite", "1"},           // Invalid guest name
                {"RES203", "Priya", "Luxury", "1"},     // Invalid room type
                {"RES204", "Karthik", "Standard", "0"}, // Invalid nights
                {"RES205", "Divya", "Suite", "1"},      // Valid
                {"RES206", "Rahul", "Suite", "1"}       // No availability
        };

        for (String[] test : testCases) {
            try {
                System.out.println("\nProcessing Booking: " + test[0]);

                Reservation res = bookingService.createBooking(
                        test[0],
                        test[1],
                        test[2],
                        Integer.parseInt(test[3])
                );

                System.out.println("Booking Successful: " + res);

            } catch (InvalidBookingException e) {
                // Graceful failure handling
                System.out.println("Booking Failed: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected Error: " + e.getMessage());
            }

            // System continues running safely
            inventory.displayInventory();
        }
    }
}
