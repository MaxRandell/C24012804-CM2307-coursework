package data;

import java.util.ArrayList;
import java.util.List;

import model.Booking;
import model.Homeowner;
import model.Property;
import model.Room;
import model.Student;

public class DataStore {
    private final List<Student> students;
    private final List<Homeowner> homeowners;
    private final List<Property> properties;
    private final List<Room> rooms;
    private final List<Booking> bookings;

    public DataStore() {
        this.students = new ArrayList<>();
        this.homeowners = new ArrayList<>();
        this.properties = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Homeowner> getHomeowners() {
        return homeowners;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
}
