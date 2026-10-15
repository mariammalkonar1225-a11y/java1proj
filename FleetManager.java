import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FleetManager {

    private final String dataFolder = "data";

    private ArrayList<Vehicle> vehicles;
    private ArrayList<Driver> drivers;
    private ArrayList<Route> routes;
    private ArrayList<Trip> trips;

    public FleetManager() {
        new File(dataFolder).mkdirs();

        vehicles = load("vehicles.dat");
        drivers = load("drivers.dat");
        routes = load("routes.dat");
        trips = load("trips.dat");
    }

    @SuppressWarnings("unchecked")
    private <T> ArrayList<T> load(String fileName) {
        File file = new File(dataFolder, fileName);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(file))) {
            return (ArrayList<T>) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                "Could not load " + fileName + ". "
                + "Check the data file before continuing.", e);
        }
    }

    private void save(String fileName, Object data) {
        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream(
                    new File(dataFolder, fileName)))) {
            output.writeObject(data);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Could not save " + fileName, e);
        }
    }

    private String nextId(String prefix, List<?> records) {
        return prefix + String.format("%03d", records.size() + 1);
    }

    public ArrayList<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    public ArrayList<Driver> getDrivers() {
        return new ArrayList<>(drivers);
    }

    public ArrayList<Route> getRoutes() {
        return new ArrayList<>(routes);
    }

    public ArrayList<Trip> getTrips() {
        return new ArrayList<>(trips);
    }

    public Vehicle findVehicle(String id) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(id)) {
                return v;
            }
        }
        return null;
    }

    public Driver findDriver(String id) {
        for (Driver d : drivers) {
            if (d.getId().equals(id)) {
                return d;
            }
        }
        return null;
    }

    public Route findRoute(String id) {
        for (Route r : routes) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public void addVehicle(String registration, String type,
                           int capacity, String status) {
        if (registration.isBlank() || type.isBlank()) {
            throw new IllegalArgumentException(
                "Registration and vehicle type are required.");
        }

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Capacity must be greater than zero.");
        }

        for (Vehicle v : vehicles) {
            if (v.getRegistrationNumber().equalsIgnoreCase(
                    registration.trim())) {
                throw new IllegalArgumentException(
                    "This registration number already exists.");
            }
        }

        vehicles.add(new Vehicle(
            nextId("V", vehicles), registration.trim(),
            type, capacity, status));

        save("vehicles.dat", vehicles);
    }

    public void deleteVehicle(String id) {
        for (Trip t : trips) {
            if (!t.getStatus().equals("Cancelled")
                    && t.getVehicleId().equals(id)) {
                throw new IllegalArgumentException(
                    "This vehicle is linked to a non-cancelled trip.");
            }
        }

        vehicles.removeIf(v -> v.getId().equals(id));
        save("vehicles.dat", vehicles);
    }

    public void addDriver(String name, String phone,
                          boolean eligible) {
        if (name.isBlank() || phone.isBlank()) {
            throw new IllegalArgumentException(
                "Driver name and phone are required.");
        }

        drivers.add(new Driver(
            nextId("D", drivers), name.trim(),
            phone.trim(), eligible));

        save("drivers.dat", drivers);
    }

    public void deleteDriver(String id) {
        for (Trip t : trips) {
            if (!t.getStatus().equals("Cancelled")
                    && t.getDriverId().equals(id)) {
                throw new IllegalArgumentException(
                    "This driver is linked to a non-cancelled trip.");
            }
        }

        drivers.removeIf(d -> d.getId().equals(id));
        save("drivers.dat", drivers);
    }

    public void addRoute(String origin, String destination,
                         String stops) {
        if (origin.isBlank() || destination.isBlank()) {
            throw new IllegalArgumentException(
                "Origin and destination are required.");
        }

        routes.add(new Route(
            nextId("R", routes), origin.trim(),
            destination.trim(), stops.trim()));

        save("routes.dat", routes);
    }

    public void deleteRoute(String id) {
        for (Trip t : trips) {
            if (!t.getStatus().equals("Cancelled")
                    && t.getRouteId().equals(id)) {
                throw new IllegalArgumentException(
                    "This route is linked to a non-cancelled trip.");
            }
        }

        routes.removeIf(r -> r.getId().equals(id));
        save("routes.dat", routes);
    }

    public void addTrip(String vehicleId, String driverId,
                        String routeId, LocalDateTime start,
                        LocalDateTime end) {

        if (findVehicle(vehicleId) == null
                || findDriver(driverId) == null
                || findRoute(routeId) == null) {
            throw new IllegalArgumentException(
                "Select a valid vehicle, driver, and route.");
        }

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                "End time must be later than start time.");
        }

        Vehicle vehicle = findVehicle(vehicleId);
        Driver driver = findDriver(driverId);

        if (!vehicle.getStatus().equals("Available")) {
            throw new IllegalArgumentException(
                "The selected vehicle is not available.");
        }

        if (!driver.isEligible()) {
            throw new IllegalArgumentException(
                "The selected driver is not eligible.");
        }

        for (Trip t : trips) {
            if (t.getStatus().equals("Cancelled")) {
                continue;
            }

            if (t.overlaps(start, end)
                    && (t.getVehicleId().equals(vehicleId)
                    || t.getDriverId().equals(driverId))) {
                throw new IllegalArgumentException(
                    "Scheduling conflict! The vehicle or driver "
                    + "already has a trip during this time.");
            }
        }

        trips.add(new Trip(
            nextId("T", trips), vehicleId, driverId,
            routeId, start, end, "Planned"));

        save("trips.dat", trips);
    }

    public void updateTripStatus(String id, String newStatus) {
        Trip selected = null;

        for (Trip t : trips) {
            if (t.getId().equals(id)) {
                selected = t;
                break;
            }
        }

        if (selected == null) {
            throw new IllegalArgumentException("Trip not found.");
        }

        if (selected.getStatus().equals("Cancelled")
                || selected.getStatus().equals("Completed")) {
            throw new IllegalArgumentException(
                "Completed or cancelled trips cannot be changed.");
        }

        if (newStatus.equals("Ongoing")) {
            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(selected.getStartTime())
                    || !now.isBefore(selected.getEndTime())) {
                throw new IllegalArgumentException(
                    "A trip can be marked Ongoing only during "
                    + "its scheduled time interval.");
            }
        }

        selected.setStatus(newStatus);
        save("trips.dat", trips);
    }
}