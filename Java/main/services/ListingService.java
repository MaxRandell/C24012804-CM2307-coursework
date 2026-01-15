package services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import data.DataStore;
import model.Booking;
import model.Homeowner;
import model.Property;
import model.Room;
import model.RoomType;


public class ListingService {
    private final DataStore store;

    public ListingService(DataStore store) {
        this.store = store;
    }
    
    public boolean removeRoom(String roomId) {
        Room room = null;

        for (Room r : store.getRooms()) {
            if (r.getId().equalsIgnoreCase(roomId)) {
                room = r;
                break;
            }
        }

        if (room == null) return false;

        // remove from global list
        store.getRooms().remove(room);

        // remove from property list
        Property p = room.getProperty();
        if (p != null) {
            p.getRooms().remove(room);
        }

        return true;    

    }

    public List<Property> getPropertiesForOwner(Homeowner owner) {
        List<Property> result = new ArrayList<>();
        for (Property p : store.getProperties()) {
            if (p.getOwner().equals(owner)) {
                result.add(p);
            }
        }
        return result;
    }

        public Property findPropertyById(String id) {
        for (Property p : store.getProperties()) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    public Room addRoom(Property property, RoomType type, double rent, String amenities,
                        LocalDate from, LocalDate to) { 

        String nextId = "Room " + (store.getRooms().size() + 1); 
        Room room = new Room(nextId, property, type, rent, amenities, from, to);

        property.getRooms().add(room);  
        store.getRooms().add(room);

        return room;
    }

    public Property addProperty(Homeowner owner, String address, String area, String description) {
        String nextId = "P" + (store.getProperties().size() + 1);

        Property p = new Property(nextId, owner, address, area, description);
        store.getProperties().add(p);

        return p;
    }

    public void editProperty(Property property, String address, String area, String description) {
        if (address != null) property.setAddress(address);
        if (area != null) property.setArea(area);
        if (description != null) property.setDescription(description);
    }

    public boolean removeProperty(String propertyId) {
        Property target = null;

        for (Property p : store.getProperties()) {
            if (p.getId().equalsIgnoreCase(propertyId)) {
                target = p;
                break;
            }
        }

        if (target == null) return false;

        // remove bookings for any rooms
        for (int i = store.getBookings().size() - 1; i >= 0; i--) {
            Booking b = store.getBookings().get(i);
            if (b == null) continue;

            Room r = b.getRoom();
            if (r == null) continue;

            Property rp = r.getProperty();
            if (rp != null && rp.equals(target)) {
                store.getBookings().remove(i);
            }
        }

        // remove rooms from the global list
        for (int i = store.getRooms().size() - 1; i >= 0; i--) {
            Room r = store.getRooms().get(i);
            if (r == null) continue;

            Property rp = r.getProperty();
            if (rp != null && rp.equals(target)) {
                store.getRooms().remove(i);
            }
        }

        // remove property from homeowner list
        store.getProperties().remove(target);

    return true;
    }
}
