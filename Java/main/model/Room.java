package model;

import java.time.LocalDate;

public class Room {
    private final String id;
    private final Property property;

    private RoomType type;
    private double monthlyRent;
    private String amenities;
    private LocalDate availableFrom;
    private LocalDate availableTo;

    public Room(String id, Property property, RoomType type, double monthlyRent,
                String amenities, LocalDate availableFrom, LocalDate availableTo) {
        this.id = id;
        this.property = property;
        this.type = type;
        this.monthlyRent = monthlyRent;
        this.amenities = amenities;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
    }

    public String getId() { return id; }
    public Property getProperty() { return property; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public double getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(double monthlyRent) { this.monthlyRent = monthlyRent; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }

    public LocalDate getAvailableTo() { return availableTo; }
    public void setAvailableTo(LocalDate availableTo) { this.availableTo = availableTo; }
}
