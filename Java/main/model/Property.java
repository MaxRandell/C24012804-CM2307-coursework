package model;

import java.util.ArrayList;
import java.util.List;

public class Property {
    private final String id;
    private final Homeowner owner;
    private String address;
    private String area;
    private String description;
    private final List<Room> rooms = new ArrayList<>();

    public Property(String id, Homeowner owner, String address, String area, String description) {
        this.id = id;
        this.owner = owner;
        this.address = address;
        this.area = area;
        this.description = description;
    }

    public String getId() { return id; }
    public Homeowner getOwner() { return owner; }
    public String getAddress() { return address; }
    public String getArea() { return area; }
    public String getDescription() { return description; }
    public List<Room> getRooms() { return rooms; }
    public void setAddress(String address) { this.address = address; }
    public void setArea(String area) { this.area = area; }
    public void setDescription(String description) { this.description = description; }
}
