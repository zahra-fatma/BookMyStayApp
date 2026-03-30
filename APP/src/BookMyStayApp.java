import java.util.*;

// Booking Request Model
class BookingRequest {
    String guestName;
    String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// Thread-safe Inventory Manager
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
    }

    // Critical Section (Thread-safe)
    public synchronized boolean allocateRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            System.out.println(Thread.currentThread().getName()
                    + " allocating " + roomType);

            // Simulate delay (to expose race condition if not synchronized)
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            inventory.put(roomType, available - 1);
            return true;
        } else {
            return false;
        }
    }

    public void displayInventory() {
        System.out.println("Final Inventory: " + inventory);
    }
}

// Shared Booking Queue
class BookingQueue {
    private Queue<BookingRequest> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest request) {
        queue.offer(request);
    }

    public synchronized BookingRequest getRequest() {
        return queue.poll();
    }
}

// Worker Thread (Concurrent Booking Processor)
class BookingProcessor extends Thread {
    private BookingQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingQueue queue, RoomInventory inventory, String name) {
        super(name);
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest request;

            // Fetch request safely
            synchronized (queue) {
                request = queue.getRequest();
            }

            if (request == null) break;

            boolean success = inventory.allocateRoom(request.roomType);

            if (success) {
                System.out.println(getName() + " SUCCESS: "
                        + request.guestName + " booked " + request.roomType);
            } else {
                System.out.println(getName() + " FAILED: "
                        + request.guestName + " - No " + request.roomType + " available");
            }
        }
    }
}

// Main Application
public class BookMyStayApp import java.io.*;
        import java.util.*;

// Reservation (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
    }
}

// System State (Inventory + Booking History)
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    Map<String, Integer> inventory;
    List<Reservation> bookings;

    public SystemState(Map<String, Integer> inventory, List<Reservation> bookings) {
        this.inventory = inventory;
        this.bookings = bookings;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.dat";

    // Save state to file
    public void save(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("System state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    // Load state from file
    public SystemState load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            System.out.println("System state loaded successfully.");
            return (SystemState) ois.readObject();

        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Corrupted data. Starting with clean state.");
        }

        return null; // safe fallback
    }
}

// Booking System
class BookingSystem {
    Map<String, Integer> inventory = new HashMap<>();
    List<Reservation> bookings = new ArrayList<>();

    public BookingSystem() {
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 2);
    }

    public void createBooking(String id, String guest, String roomType) {
        int available = inventory.getOrDefault(roomType, 0);

        if (available <= 0) {
            System.out.println("No rooms available for " + roomType);
            return;
        }

        inventory.put(roomType, available - 1);
        bookings.add(new Reservation(id, guest, roomType));

        System.out.println("Booking confirmed: " + id);
    }

    public void displayState() {
        System.out.println("\n--- Current State ---");
        System.out.println("Inventory: " + inventory);
        System.out.println("Bookings:");
        for (Reservation r : bookings) {
            System.out.println(r);
        }
    }

    public SystemState getState() {
        return new SystemState(inventory, bookings);
    }

    public void restoreState(SystemState state) {
        this.inventory = state.inventory;
        this.bookings = state.bookings;
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        PersistenceService persistence = new PersistenceService();
        BookingSystem system = new BookingSystem();

        // STEP 1: Load previous state (Recovery)
        SystemState loadedState = persistence.load();
        if (loadedState != null) {
            system.restoreState(loadedState);
        }

        system.displayState();

        // STEP 2: Perform operations
        system.createBooking("RES401", "Arun", "Standard");
        system.createBooking("RES402", "Priya", "Deluxe");

        system.displayState();

        // STEP 3: Save state (Persistence)
        persistence.save(system.getState());

        System.out.println("\n--- Restart the application to see recovery ---");
    }
}