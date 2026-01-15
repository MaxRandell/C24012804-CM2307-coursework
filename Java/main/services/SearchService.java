package services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import data.DataStore;
import model.Booking;
import model.BookingStatus;
import model.Room;

public class SearchService {
    private final DataStore store;

    public SearchService(DataStore store) {
        this.store = store;
    }

    public List<Room> searchRooms(SearchCriteria criteria) {
         List<Room> results = new ArrayList<>();

    for (Room room : store.getRooms()) {

        // Filter by area
        if (criteria.area != null && !criteria.area.isBlank()) {
            String roomArea = room.getProperty().getArea();
            if (roomArea == null || !roomArea.equalsIgnoreCase(criteria.area.trim())) {
                continue;
            }
        }

        // Filter by max rent
        if (criteria.maxRent != null) {
            if (room.getMonthlyRent() > criteria.maxRent) {
                continue;
            }
        }

        // Filter by how many rooms
        if (criteria.roomType != null) {
            if (room.getType() != criteria.roomType) {
                continue;
            }
        }

        // Filter by availability
        if (criteria.startDate != null || criteria.endDate != null) {
            LocalDate searchStart = criteria.startDate;
            LocalDate searchEnd = criteria.endDate;

            // Filters by date
            // Fixes my original ideas issue where it ignored only one input
            // allows for only the start or the end date to be inputted and still filters by it
            if (searchStart == null) searchStart = searchEnd;
            if (searchEnd == null) searchEnd = searchStart;

            if (room.getAvailableFrom() != null && searchStart.isBefore(room.getAvailableFrom())) continue;
            if (room.getAvailableTo() != null && searchEnd.isAfter(room.getAvailableTo())) continue;

            // Doesn't show rooms booked during the window searched for
            boolean clashes = false;
            for (Booking b : store.getBookings()) {
                if (!b.getRoom().equals(room)) continue;
                if (b.getStatus() != BookingStatus.ACCEPTED) continue;

                if (datesOverlap(searchStart, searchEnd, b.getStartDate(), b.getEndDate())) {
                    clashes = true;
                    break;
                    }
                }
            if (clashes) continue;
            }

        results.add(room);
    }

    return results;

    }

    private boolean datesOverlap(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
    // checks for overlapping dates
    return !(aEnd.isBefore(bStart) || aStart.isAfter(bEnd));
    }
}
