package app;

import data.DataStore;
import data.SeedData;
import services.BookingService;
import services.ListingService;
import services.SearchService;
import ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        DataStore store = new DataStore();
        SeedData.populate(store);
        ListingService listingService = new ListingService(store);
        SearchService searchService = new SearchService(store);
        BookingService bookingService = new BookingService(store);

        ConsoleUI ui = new ConsoleUI(store, listingService, searchService, bookingService);
        ui.run();
    }
}