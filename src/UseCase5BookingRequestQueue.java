import java.util.LinkedList;
import java.util.Queue;

// Reservation class representing a booking request
class Reservation {
    String guestName;
    String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

public class UseCase5BookingRequestQueue {

    public static void main(String[] args) {

        // Queue to store booking requests
        Queue<Reservation> bookingQueue = new LinkedList<>();

        // Guest submits booking requests
        bookingQueue.add(new Reservation("ABHI", "SINGLE"));
        bookingQueue.add(new Reservation("SUBHA", "DOUBLE"));
        bookingQueue.add(new Reservation("RAHUL", "SUITE"));

        // Processing booking requests in FIFO order
        while (!bookingQueue.isEmpty()) {
            Reservation request = bookingQueue.poll();

            System.out.println("Processing request for guest : "
                    + request.guestName
                    + " , Room Type : "
                    + request.roomType);
        }
    }
}