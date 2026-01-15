package ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import data.DataStore;

import model.Room;
import model.RoomType;
import model.Student;
import model.Booking;
import model.BookingStatus;
import model.Homeowner;
import model.Property;

import services.BookingService;
import services.ListingService;
import services.SearchCriteria;
import services.SearchService;

public class ConsoleUI {
    private final DataStore store;
    private final ListingService listingService;
    private final SearchService searchService;
    private final BookingService bookingService;
    private Student currentStudent;
    private Homeowner currentHomeowner;

    private enum Mode { 
        STUDENT,
        HOMEOWNER
    }

    private Mode mode = Mode.STUDENT; // Sets user to student as default


    public ConsoleUI(DataStore store, ListingService listingService,
                     SearchService searchService, BookingService bookingService) {
        this.store = store;
        this.listingService = listingService;
        this.searchService = searchService;
        this.bookingService = bookingService;

        if (!store.getStudents().isEmpty()) {
            currentStudent = store.getStudents().get(0);
        }    

        if (!store.getHomeowners().isEmpty()) {
            currentHomeowner = store.getHomeowners().get(0);
        }
    }

    // Main chunk of code

    public void run() {
        Scanner sc = new Scanner(System.in);

        // Code loop for main menu

        while (true) {
            System.out.println("\nStudentRentals (Test)");
            System.out.println("Current role: " + mode);

            // Student menu
            
            if (mode == Mode.STUDENT) {
                System.out.println("Active student: " + (currentStudent == null ? "None" : currentStudent.getId()));
                System.out.println("1) List all rooms");
                System.out.println("2) Search rooms");
                System.out.println("3) Switch role");
                System.out.println("4) Switch active student");

            // Homeowner menu

            } else {
                System.out.println("Active homeowner: " + (currentHomeowner == null ? "None" : currentHomeowner.getId()));
                System.out.println("1) Manage bookings");
                System.out.println("2) Manage properties");
                System.out.println("3) Add property");
                System.out.println("4) Switch role");
                System.out.println("5) Switch active homeowner");
            }

            System.out.println("0) Exit");

            // User input line

            System.out.print("> ");

            String input = sc.nextLine().trim();
            
            // Input handling

            if (input.equals("0")) {
                    break;
            }

            // Student input handling

            if (mode == Mode.STUDENT) {
                if (input.equals("1")) {
                    try {
                        showRoomResultsLoop(store.getRooms(), sc);
                    } catch (ReturnToMenuException e) {
                    // return to menu
                    }
                }

                else if (input.equals("2")) {
                    SearchCriteria criteria = readSearchCriteria(sc);
                    List<Room> results = searchService.searchRooms(criteria);

                    if (results.isEmpty()) {
                        System.out.println("No rooms matched your filters.");
                    } else {
                        try {
                            showRoomResultsLoop(results, sc);
                            } catch (ReturnToMenuException e) {
                            // return to menu
                        }
                    }
                }

                else if (input.equals("3")) {
                    switchRole(sc);
                    continue;
                }

                else if (input.equals("4")) {
                    { chooseStudent(sc); }
                }

                else if (looksLikeRoomId(input)) {
                    Room room = findRoomById(input);
                    if (room == null) {
                        System.out.println("Room not found.");
                    } else {
                     // Enter loop
                        try {
                            showRoomDetailsLoop(room, sc);
                            } catch (ReturnToMenuException e) {
                            // return to menu
                        }
                    }
                }

                else System.out.println("Unknown option.");

            // Homeowner input handling

            } else {   
                if (input.equals("1")) { manageBookingRequests(sc); }
                else if (input.equals("2")) { manageProperties(sc); }
                else if (input.equals("3")) {addProperty(sc); }
                else if (input.equals("4")) {switchRole(sc); }
                else if (input.equals("5")) { chooseHomeowner(sc); }
                else System.out.println("Unknown option.");
            }    
        }   

        sc.close();

        }

    // Prints all rooms

    private void printRoomList(List<Room> rooms) {
        for (Room r : rooms) {
            System.out.println(
                r.getId() + " | " +
                r.getProperty().getArea() + " | " +
                r.getType() + " | £" +
                r.getMonthlyRent() +
                " | " + r.getAmenities() +
                " | " + r.getAvailableFrom() + " to " + r.getAvailableTo() +
                " | " + getBooking(r)
            );
        }
    }

    // Prints room details for x room

    private void printRoomDetails(Room r) {
        System.out.println("\nRoom Details");
        System.out.println("ID: " + r.getId());
        System.out.println("Area: " + r.getProperty().getArea());
        System.out.println("Address: " + r.getProperty().getAddress());
        System.out.println("Type: " + r.getType());
        System.out.println("Rent: £" + r.getMonthlyRent());
        System.out.println("Amenities: " + r.getAmenities());
        System.out.println("Available from: " + r.getAvailableFrom() + " to " + r.getAvailableTo());
        System.out.println("Property description: " + r.getProperty().getDescription());

        boolean any = false;

        for (Booking b : store.getBookings()) {
            if (!b.getRoom().equals(r)) continue;
            if (b.getStatus() != BookingStatus.ACCEPTED) continue;

            if (!any) {
                System.out.println("Booked periods:");
                any = true;
            }

            System.out.println(" - booked from " + b.getStartDate() + " to " + b.getEndDate());
        }

        if (!any) {
            System.out.println("Property not booked.");
        }
    }

    // Function displaying inputs for the different search criteria

    private SearchCriteria readSearchCriteria(Scanner sc) {
        SearchCriteria c = new SearchCriteria();

        System.out.print("Area - Enter for any: ");
        String area = sc.nextLine().trim();
        c.area = area.isBlank() ? null : area;

        System.out.print("Max rent - Enter for any: ");
        String maxRentStr = sc.nextLine().trim();
        c.maxRent = parseDoubleOrNull(maxRentStr);

        System.out.print("Room type SINGLE/DOUBLE - Enter for any: ");
        String typeStr = sc.nextLine().trim();
        c.roomType = parseRoomTypeOrNull(typeStr);

        System.out.print("Start date (YYYY-MM-DD) (leave blank for any): ");
        String startStr = sc.nextLine().trim();
        if (!startStr.isBlank()) {
            LocalDate d = parseDateOrNull(startStr);
            if (d == null) {
                System.out.println("Invalid date.");
                return c;
            }
            c.startDate = d;
        }

        System.out.print("End date (YYYY-MM-DD) (leave blank for any): ");
        String endStr = sc.nextLine().trim();
        if (!endStr.isBlank()) {
            LocalDate d = parseDateOrNull(endStr);
            if (d == null) {
                System.out.println("Invalid date.");
                return c;
            }
            c.endDate = d;
        }

        if (c.startDate != null && c.endDate != null && c.endDate.isBefore(c.startDate)) {
            System.out.println("End date can't be before start date.");
            return c;
        }

        return c;
    }

    // Checks validity of input for rent

    private Double parseDoubleOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
            return null;
        }
    }
    
    // Checks validity of input for room type

    private RoomType parseRoomTypeOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return RoomType.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input");
            return null;
        }
    }

    private boolean looksLikeRoomId(String input) {
        if (input == null) return false;
        String s = input.trim().toLowerCase();
        return s.matches("room\\s*\\d+");
    }

    private Room findRoomById(String roomId) {
        String normalisedInput = roomId.replaceAll("\\s+", "").toLowerCase();

        for (Room r : store.getRooms()) {
            String normalisedStored = r.getId().replaceAll("\\s+", "").toLowerCase();
            if (normalisedStored.equals(normalisedInput)) {
                return r;
            }
        }
        return null; 
    }

    private void showRoomResultsLoop(List<Room> rooms, Scanner sc) {
    if (rooms.isEmpty()) {
        System.out.println("No rooms match the search criteria");
        return;
    }

    printRoomList(rooms);
    System.out.println("\nType (\"Room x\") to view details and book, or type \"back\" to return to menu.");

    while (true) {
        System.out.print("> ");
        String input = sc.nextLine().trim();

        if (input.equalsIgnoreCase("back")) {
            return; // go back to main menu
        }

        if (looksLikeRoomId(input)) {
            Room room = findRoomById(input);
            if (room == null) {
                System.out.println("Room not found.");
            } else {
                showRoomDetailsLoop(room, sc); // View room details
                // Allows for another room to be looked at without going back to menu first
                System.out.println("\nType another room number to view details, or \"back\" to return.");
            }
        } else {
            System.out.println("Please enter \"Room x\" or type \"back\".");
        }
    }
    }

    private void showRoomDetailsLoop(Room room, Scanner sc) {
        printRoomDetails(room);
        System.out.println("\nType \"book\" to request a booking, \"back\" to return to results, or \"menu\" to return to main menu.");

        while (true) {
            System.out.print("> ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
            return; // Return to list of rooms
            }
            if (input.equalsIgnoreCase("menu")) {
                // Return to main menu
                throw new ReturnToMenuException();
            }

            if (input.equalsIgnoreCase("back")) {
                return;
            }

            if (input.equalsIgnoreCase("book")) {
                handleBookingRequest(room, sc);
                continue;
            }

            System.out.println("\nType \"book\" to request a booking, \"back\" to return to results, or \"menu\" to return to main menu.");

        }
    }

        private static class ReturnToMenuException extends RuntimeException {
    
        }

    private void handleBookingRequest(Room room, Scanner sc) {
        // Can't book a room without setting an active student
        if (currentStudent == null) {
            System.out.println("No active student selected.");
            return;
        }

        // Validates date input
        System.out.print("Start date (YYYY-MM-DD): ");
        LocalDate start = parseDateOrNull(sc.nextLine().trim());
        if (start == null) {
            System.out.println("Invalid date.");
            return;
        }

        System.out.print("End date (YYYY-MM-DD): ");
        LocalDate end = parseDateOrNull(sc.nextLine().trim());
        if (end == null) {
            System.out.println("Invalid date.");
            return;
        }

        // Logic check for each rooms availability
        if (!start.isBefore(end)) {
            System.out.println("Start date must be before end date.");
            return;
        }

        if (start.isBefore(room.getAvailableFrom()) || end.isAfter(room.getAvailableTo())) {
            System.out.println("That date range is outside of the rooms availability.");
            return;
        }

        // Successful booking
        Booking booking = bookingService.requestBooking(currentStudent, room, start, end);

        if (booking == null) {
            System.out.println("Booking request could not be created.");
            return;
        }

        System.out.println(
            "Booking request created: " +
            booking.getId() + " (" + booking.getStatus() + ")"
        );
    }

    private LocalDate parseDateOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    // Sets up student access
    private void chooseStudent(Scanner sc) {
    System.out.println("Students:");
    for (Student s : store.getStudents()) {
        System.out.println(" - " + s.getId() + " (" + s.getName() + ")");
    }

    System.out.print("Enter student id (e.g. S1): ");
    String id = sc.nextLine().trim();

    for (Student s : store.getStudents()) {
        if (s.getId().equalsIgnoreCase(id)) {
            currentStudent = s;
            System.out.println("Active student set to " + s.getId() + " (" + s.getName() + ")");
            return;
        }
    }

    System.out.println("Student not found.");
    }

    private void chooseHomeowner(Scanner sc) {
        System.out.println("Homeowners:");
        for (Homeowner h : store.getHomeowners()) {
            System.out.println(" - " + h.getId() + " (" + h.getName() + ")");
        }

        System.out.print("Enter Homeowner id (e.g. H1): ");
        String id = sc.nextLine().trim();

        for (Homeowner h : store.getHomeowners()) {
            if (h.getId().equalsIgnoreCase(id)) {
                currentHomeowner = h;
                System.out.println("Active Homeowner set to " + h.getId() + " (" + h.getName() + ")");
                return;
            }
        }
    }

    private void manageBookingRequests(Scanner sc) {
        // Cannot view booking requests if not a homeowner
        if (currentHomeowner == null) {
            System.out.println("No active homeowner selected.");
            return;
        }

        List<Booking> requests = bookingService
            .getRequests(currentHomeowner);

        // Default output if used before a student makes a booking
        if (requests.isEmpty()) {
            System.out.println("No pending booking requests.");
            return;
        }

        // View booking requests
        System.out.println("\nPending booking requests:");
        for (Booking b : requests) {
            System.out.println(
                b.getId() + " | Room: " + b.getRoom().getId() +
                " | Student: " + b.getStudent().getId() +
                " | " + b.getStartDate() + " to " + b.getEndDate() +
                " | " + b.getStatus()
            );
        }

        // Select bookings to manage
        System.out.println("\nEnter booking id to decide, or type \"back\":");

        while (true) {
            System.out.print("> ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
                return;
            }

            Booking booking = bookingService.findBookingById(input);
            if (booking == null) {
                System.out.println("Booking not found.");
                continue;
            }

            // Manage bookings
            String decision = "";
            while (true) {
                System.out.print("Accept booking? (y/n): ");
                decision = sc.nextLine().trim();

                if (decision.equalsIgnoreCase("y") || decision.equalsIgnoreCase("n")) {
                    break;
                }

                System.out.println("Type y or n");
            }

            // Sets booking to accepted or rejected
            boolean accept = decision.equalsIgnoreCase("y");

            int rejected = bookingService.decideBooking(booking, accept);

            // Accept or reject message
            System.out.println("Booking " + booking.getId() +
                    " updated to " + booking.getStatus());

            // Sanity check as bookings "disappear" otherwise once one is accepted that overlaps
            if (accept && rejected > 0) {
                System.out.println(
                    rejected + " Booking(s) rejected automatically"
                );
            }

            return;
        }
    }

    // To be able to show booking status in the list view

    private String getBooking(Room r) {
        for (Booking b : store.getBookings()) {
            if (!b.getRoom().equals(r)) continue;
            if (b.getStatus() != BookingStatus.ACCEPTED) continue;
            return "BOOKED " + b.getStartDate() + " to " + b.getEndDate();
        }
        return "AVAILABLE";
    }

    private void setMode(Mode newMode) {
        mode = newMode;

        if (mode == Mode.STUDENT) {
            currentHomeowner = null;
                if (currentStudent == null && !store.getStudents().isEmpty()) {
                    currentStudent = store.getStudents().get(0);
                }
        } else { // homeowner
            currentStudent = null;
            if (currentHomeowner == null && !store.getHomeowners().isEmpty()) {
                currentHomeowner = store.getHomeowners().get(0);
            }
        }
    }

    // Switch between homeowner or student

    private void switchRole(Scanner sc) {
        System.out.println("Choose role:");
        System.out.println("1) Student");
        System.out.println("2) Homeowner");
        System.out.print("> ");

        String choice = sc.nextLine().trim();
        if (choice.equals("1")) {
            setMode(Mode.STUDENT);
            System.out.println("Now acting as STUDENT.");
        } else if (choice.equals("2")) {
            setMode(Mode.HOMEOWNER);
            System.out.println("Now acting as HOMEOWNER.");
        } else {
            System.out.println("Invalid choice.");
        }
    }


    private void manageProperties(Scanner sc) {
        if (currentHomeowner == null) {
            System.out.println("No active homeowner selected.");
            return;
        }

        while (true) {
            System.out.println("\nManage Properties (" + currentHomeowner.getId() + ")");
            System.out.println("1) List my properties");
            System.out.println("2) Edit property");
            System.out.println("0) Back");
            System.out.print("> ");

            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                return;
            } else if (input.equals("1")) {
                listMyProperties();
            } else if (input.equals("2")) {
                editProperty(sc);
            } else {
                System.out.println("Unknown option.");
            }
        }
    }


    private void listMyProperties() {
        if (currentHomeowner == null) {
            System.out.println("No active homeowner.");
            return;
        }

        List<Property> props = listingService.getPropertiesForOwner(currentHomeowner);

        if (props.isEmpty()) {
            System.out.println("You have no properties listed.");
            return;
        }

        System.out.println("\nMy properties:");
        for (Property p : props) {
            System.out.println(p.getId() + " | " + p.getArea() + " | " + p.getAddress());
            if (p.getRooms().isEmpty()) {
                System.out.println("   (no rooms)");
            } else {
                for (Room r : p.getRooms()) {
                    System.out.println("   - " + r.getId() + " | " + r.getType() +
                            " | £" + r.getMonthlyRent());
                }
            }
        }
    }

    // Homeowner edits rooms on a property

    private void editProperty(Scanner sc) {
        if (currentHomeowner == null) {
            System.out.println("No active homeowner selected.");
            return;
        }

        List<Property> props = listingService.getPropertiesForOwner(currentHomeowner);
        if (props.isEmpty()) {
            System.out.println("You have no properties to edit");
            return;
        }

        System.out.println("Choose property id:");
        for (Property p : props) {
            System.out.println(" - " + p.getId() + " (" + p.getArea() + ")");
        }
        System.out.print("> ");
        String propId = sc.nextLine().trim();

        Property property = listingService.findPropertyById(propId);
        if (property == null || !property.getOwner().equals(currentHomeowner)) {
            System.out.println("Property not found.");
            return;
        }

        while (true) {
            System.out.println("\nEdit Property: " + property.getId() + " (" + property.getArea() + ")");
            System.out.println("1) List rooms");
            System.out.println("2) Add a room");
            System.out.println("3) Edit a room");
            System.out.println("4) Remove a room");
            System.out.println("5) Edit property details");
            System.out.println("6) Remove this property");
            System.out.println("0) Back");
            System.out.print("> ");

            String choice = sc.nextLine().trim();

            if (choice.equals("0")) {
                return;
            }

            if (choice.equals("1")) {
                if (property.getRooms().isEmpty()) {
                    System.out.println("(no rooms)");
                } else {
                    for (Room r : property.getRooms()) {
                        System.out.println(" - " + r.getId() + " | " + r.getType()
                                + " | £" + r.getMonthlyRent()
                                + " | " + r.getAmenities()
                                + " | " + getBooking(r));
                    }
                }
                continue;
            }

            if (choice.equals("2")) {
                // Add a room to a property
                addRoom(sc, property);
                continue;
            }

            if (choice.equals("3")) {
                // edit an existing room
                editRoom(sc, property);
                continue;
            }

            if (choice.equals("4")) {
                // remove a room from x property
                removeRoom(sc, property);
                continue;
            }

            if (choice.equals("5")) {
                // edit property details (address, area, availability)
                editProperty(sc, property);
            }

            if (choice.equals("6")) {
                // Deletes a property and all information about it
                boolean deleted = removeProperty(sc, property);
                if (deleted) {
                    return; // Back to properties screen
                }
            continue;
            }

            System.out.println("Unknown option.");
        }
    }

    // Add room to a property

    private void addRoom(Scanner sc, Property property) {
        System.out.print("Room type SINGLE/DOUBLE: ");
        RoomType type = parseRoomTypeOrNull(sc.nextLine().trim());
        if (type == null) {
            System.out.println("Invalid room type.");
            return;
        }

        System.out.print("Monthly rent: ");
        Double rent = parseDoubleOrNull(sc.nextLine().trim());
        if (rent == null || rent <= 0) {
            System.out.println("Invalid rent.");
            return;
        }

        System.out.print("Amenities (Comma in between): ");
        String amenities = sc.nextLine().trim();

        System.out.print("Available from (yyyy-mm-dd): ");
        LocalDate from = parseDateOrNull(sc.nextLine().trim());
        if (from == null) {
            System.out.println("Invalid date.");
            return;
        }

        System.out.print("Available to (yyyy-mm-dd): ");
        LocalDate to = parseDateOrNull(sc.nextLine().trim());
        if (to == null || to.isBefore(from)) {
            System.out.println("Invalid date range.");
            return;
        }

        Room room = listingService.addRoom(property, type, rent, amenities, from, to);
        System.out.println("Room added: " + room.getId());
    }

    // Edit a room on property

    private void editRoom(Scanner sc, Property property) {
        if (property.getRooms().isEmpty()) {
            System.out.println("No rooms to edit.");
            return;
        }

        System.out.println("Enter room id to edit:");
        for (Room r : property.getRooms()) {
            System.out.println(" - " + r.getId() + " (" + r.getType() + ", £" + r.getMonthlyRent() + ")");
        }
        System.out.print("> ");
        String roomId = sc.nextLine().trim();

        Room room = null;
        for (Room r : property.getRooms()) {
            if (r.getId().equalsIgnoreCase(roomId)) {
                room = r;
                break;
            }
        }

        if (room == null) {
            System.out.println("Room not found.");
            return;
        }

        System.out.println("Press Enter to keep existing value.");

        System.out.print("New room type (SINGLE/DOUBLE) [current: " + room.getType() + "]: ");
        String typeStr = sc.nextLine().trim();
        if (!typeStr.isBlank()) {
            RoomType newType = parseRoomTypeOrNull(typeStr);
            if (newType == null) {
                System.out.println("Invalid type.");
                return;
            }
            room.setType(newType);
        }

        System.out.print("New monthly rent [current: £" + room.getMonthlyRent() + "]: ");
        String rentStr = sc.nextLine().trim();
        if (!rentStr.isBlank()) {
            Double newRent = parseDoubleOrNull(rentStr);
            if (newRent == null || newRent <= 0) {
                System.out.println("Invalid rent.");
                return;
            }
            room.setMonthlyRent(newRent);
        }

        System.out.print("New amenities [current: " + room.getAmenities() + "]: ");
        String amenStr = sc.nextLine();
        if (!amenStr.isBlank()) {
            room.setAmenities(amenStr.trim());
        }

        // Store start and end dates for validating dates make sense (So that the start date can't be after the end date - but can be after the old end date)
        // Ran into issues with my first set of code blocking a new start date being after the old end date, not allowing for the dates to be drastically changed

        LocalDate oldFrom = room.getAvailableFrom();
        LocalDate oldTo = room.getAvailableTo();

        LocalDate newFrom = oldFrom;
        LocalDate newTo = oldTo;

        System.out.print("New available from (YYYY-MM-DD) [current: " + oldFrom + "]: ");
        String fromStr = sc.nextLine().trim();
        if (!fromStr.isBlank()) {
            LocalDate parsedFrom = parseDateOrNull(fromStr);
            if (parsedFrom == null) {
                System.out.println("Invalid date.");
                return;
            }
            newFrom = parsedFrom;
        }

        System.out.print("New available to (YYYY-MM-DD) [current: " + oldTo + "]: ");
        String toStr = sc.nextLine().trim();
        if (!toStr.isBlank()) {
            LocalDate parsedTo = parseDateOrNull(toStr);
            if (parsedTo == null) {
                System.out.println("Invalid date.");
                return;
            }
            newTo = parsedTo;
        }

        if (newFrom != null && newTo != null && newTo.isBefore(newFrom)) {
            System.out.println("End date can't be before start date.");
            // Revert back to old dates
            room.setAvailableFrom(oldFrom);
            room.setAvailableTo(oldTo);
            return;
        }
        
        // Apply new dates
        room.setAvailableFrom(newFrom);
        room.setAvailableTo(newTo);


        // Confirms room changes
        System.out.println("Room updated.");
    }

    // Remove a room from property

    private void removeRoom(Scanner sc, Property property) {
        System.out.print("Enter room id to remove (e.g. Room x): ");
        String roomId = sc.nextLine().trim();

        Room room = findRoomById(roomId);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }

        // validity check
        if (room.getProperty() == null || !room.getProperty().getOwner().equals(currentHomeowner)) {
            System.out.println("You can only remove rooms you own.");
            return;
        }

        boolean removed = listingService.removeRoom(room.getId());
        System.out.println(removed ? "Room removed." : "Could not remove room.");
    }

    // Add a property for a homeowner

    private void addProperty(Scanner sc) {
        if (currentHomeowner == null) {
            System.out.println("No active homeowner selected.");
            return;
        }

        System.out.println("\nAdd New Property");

        System.out.print("Address: ");
        String address = sc.nextLine().trim();
        if (address.isBlank()) {
        System.out.println("Invalid input.");
            return;
        }

        System.out.print("Area: ");
        String area = sc.nextLine().trim();
        if (area.isBlank()) {
            System.out.println("Invalid input.");
            return;
        }

        System.out.print("Short description: ");
        String desc = sc.nextLine().trim();
        if (desc.isBlank()) {
            desc = " ";
        }

        Property property = listingService.addProperty(currentHomeowner, address, area, desc);
            System.out.println("Property created: " + property.getId());

            // Create a room in property
            while (true) {
                System.out.print("Add a room to this property now? (y/n): ");
                String yn = sc.nextLine().trim().toLowerCase();

                if (yn.equals("n")) {
                    break;
                }
                if (!yn.equals("y")) {
                    System.out.println("Please enter y or n.");
                    continue;
                }

                addRoom(sc, property);
            }

        System.out.println("Property added with " + property.getRooms().size() + " room(s).");
    }

    // Edit details of a property

    private void editProperty(Scanner sc, Property property) {
        System.out.println("Press Enter to keep existing value.");

        System.out.print("Address (" + property.getAddress() + "): ");
        String newAddress = sc.nextLine().trim();
        if (newAddress.isEmpty()) newAddress = null;

        System.out.print("Area (" + property.getArea() + "): ");
        String newArea = sc.nextLine().trim();
        if (newArea.isEmpty()) newArea = null;

        System.out.print("Description (" + property.getDescription() + "): ");
        String newDesc = sc.nextLine().trim();
        if (newDesc.isEmpty()) newDesc = null;

        listingService.editProperty(property, newAddress, newArea, newDesc);
        System.out.println("Property updated.");
    }

    // Remove a property

    private boolean removeProperty(Scanner sc, Property property) {
        System.out.println("Are you sure you want to delete: " + property.getId() + ".");
        System.out.println("This will permanantly delete the property and all existing bookings");
        System.out.print("Type DELETE to confirm, or enter to cancel: ");

        String confirm = sc.nextLine().trim();
        if (!confirm.equalsIgnoreCase("DELETE")) {
            System.out.println("Your property has not been deleted.");
            return false;
        }

        boolean ok = listingService.removeProperty(property.getId());
        if (ok) {
            System.out.println("Property removed.");
            return true;
        } else {
            System.out.println("Could not remove property (not found).");
            return false;
        }
    }
}
