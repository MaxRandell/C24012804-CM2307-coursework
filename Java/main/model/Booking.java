package model;

import java.time.LocalDate;

public class Booking {
    private final String id;
    private final Student student;
    private final Room room;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private BookingStatus status;

    public Booking(String id, Student student, Room room, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.student = student;
        this.room = room;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = BookingStatus.REQUESTED;
    }

    public String getId() { return id; }
    public Student getStudent() { return student; }
    public Room getRoom() { return room; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
