package data;

import java.time.LocalDate;

import model.Homeowner;
import model.Property;
import model.Room;
import model.RoomType;
import model.Student;

public class SeedData {
     public static void populate(DataStore store) {
        // Users
        Homeowner h1 = new Homeowner("H1", "X Homeowner");
        Homeowner h2 = new Homeowner("H2", "Y Homeowner");

        Student s1 = new Student("S1", "X Student");
        Student s2 = new Student("S2", "Y Student");

        store.getHomeowners().add(h1);
        store.getHomeowners().add(h2);
        store.getStudents().add(s1);
        store.getStudents().add(s2);

        // Properties
        Property p1 = new Property("P1", h1, "X str", "Cathays", "Near campus");
        Property p2 = new Property("P2", h2, "X Rd", "Roath", "Quiet area");

        store.getProperties().add(p1);
        store.getProperties().add(p2);

        // Rooms
        Room r1 = new Room("Room 1", p1, RoomType.SINGLE, 450.0, "Wi-Fi, Desk, Wardrobe",
                LocalDate.now().plusDays(1), LocalDate.now().plusMonths(6));
        Room r2 = new Room("Room 2", p1, RoomType.DOUBLE, 550.0, "Wi-Fi, Ensuite, Desk",
                LocalDate.now().plusDays(7), LocalDate.now().plusMonths(4));

        Room r3 = new Room("Room 3", p2, RoomType.SINGLE, 400.0, "Wi-Fi",
                LocalDate.now().plusDays(3), LocalDate.now().plusMonths(5));
        Room r4 = new Room("Room 4", p2, RoomType.DOUBLE, 600.0, "Wi-Fi, Parking, Ensuite",
                LocalDate.now().plusDays(10), LocalDate.now().plusMonths(8));

        p1.getRooms().add(r1);
        p1.getRooms().add(r2);
        p2.getRooms().add(r3);
        p2.getRooms().add(r4);

        store.getRooms().add(r1);
        store.getRooms().add(r2);
        store.getRooms().add(r3);
        store.getRooms().add(r4);
    }

    private SeedData() { }
}
