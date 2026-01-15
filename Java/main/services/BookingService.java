package services;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import data.DataStore;
import model.Booking;
import model.BookingStatus;
import model.Room;
import model.Student;
import model.Homeowner;

public class BookingService {
    private final DataStore store;

    public BookingService(DataStore store) {
        this.store = store;
    }

    public Booking requestBooking(Student student, Room room, LocalDate start, LocalDate end) {

        for (Booking b : store.getBookings()) {
            if (b.getRoom().equals(room) &&
                b.getStatus() == BookingStatus.ACCEPTED &&
                !(end.isBefore(b.getStartDate()) || start.isAfter(b.getEndDate()))) {

                System.out.println("Room already booked for that period.");
                return null;
            }
        }

        Booking b = new Booking("B" + (store.getBookings().size() + 1), student, room, start, end);
        store.getBookings().add(b);
        return b;
    }

    public int decideBooking(Booking booking, boolean accept) {
        // Reject booking
        if (!accept) {
            booking.setStatus(BookingStatus.REJECTED);
            return 0;
        }

        // Accept booking
        booking.setStatus(BookingStatus.ACCEPTED);

        int rejectedCount = 0;

        // Stops a property being double booked with overlapping dates
        for (Booking other : store.getBookings()) {

            if (other == booking) continue;

            if (other.getRoom().equals(booking.getRoom()) &&
                other.getStatus() == BookingStatus.REQUESTED &&
                datesOverlap(booking, other)) {
                
                // Amount of overlapping bookings that are automatically rejected
                other.setStatus(BookingStatus.REJECTED);
                rejectedCount++;
            }
        }

        return rejectedCount;

    }

    public List<Booking> getRequests(Homeowner owner) {
        List<Booking> result = new ArrayList<>();

        for (Booking b : store.getBookings()) {
            if (b.getStatus() == BookingStatus.REQUESTED &&
                b.getRoom().getProperty().getOwner().equals(owner)) {
                result.add(b);
            }
        }
        return result;
    }

    public Booking findBookingById(String id) {
        for (Booking b : store.getBookings()) {
            if (b.getId().equalsIgnoreCase(id)) {
                return b;
            }
        }
        return null;
    }

    private boolean datesOverlap(Booking a, Booking b) {
        return  !a.getEndDate().isBefore(b.getStartDate()) &&
                !a.getStartDate().isAfter(b.getEndDate());
    } 
}
