package org.example.service;

import org.example.datastructure.LinkedList;
import org.example.model.Car;
import org.example.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing cars using Singleton pattern
 */
public class CarService {
    private static CarService instance;
    private final LinkedList<Car> cars;
    private static final String CARS_FILE = "cars.txt";
    private static final String DELIMITER = "||";

    private CarService() {
        cars = new LinkedList<>();
        loadCarsFromFile();
    }

    public static CarService getInstance() {
        if (instance == null) {
            instance = new CarService();
        }
        return instance;
    }

    private void loadCarsFromFile() {
        List<String> lines = FileUtil.readLines(CARS_FILE);
        for (String line : lines) {
            String[] parts = line.split("\\|\\|");
            if (parts.length >= 6) {
                try {
                    String make = parts[0];
                    String model = parts[1];
                    int year = Integer.parseInt(parts[2]);
                    String registrationNumber = parts[3];
                    String specifications = parts[4];
                    double price = Double.parseDouble(parts[5]);
                    cars.add(new Car(make, model, year, registrationNumber, specifications, price));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing car data: " + line);
                }
            }
        }
        System.out.println("CarService: Loaded " + cars.size() + " cars from file");
    }

    private void saveCarsToFile() {
        List<String> lines = new ArrayList<>();
        Car[] allCars = cars.toArray();
        for (Car car : allCars) {
            String line = String.join(DELIMITER,
                car.getMake(),
                car.getModel(),
                String.valueOf(car.getYear()),
                car.getRegistrationNumber(),
                car.getSpecifications() != null ? car.getSpecifications() : "",
                String.valueOf(car.getPrice())
            );
            lines.add(line);
        }
        FileUtil.writeLines(CARS_FILE, lines);
        System.out.println("CarService: Saved " + lines.size() + " cars to file");
    }

    /**
     * Add new car
     * @param make Car make
     * @param model Car model
     * @param year Car year
     * @param registrationNumber Car registration number
     * @param specifications Car specifications
     * @param price Car price in Rs
     * @return true if car added, false if registration number exists
     */
    public boolean addCar(String make, String model, int year, String registrationNumber, String specifications, double price) {
        System.out.println("CarService: Adding car with registration " + registrationNumber);
        if (cars.find(car -> car.getRegistrationNumber().equals(registrationNumber)) != null) {
            System.out.println("CarService: Car with registration " + registrationNumber + " already exists");
            return false;
        }
        cars.add(new Car(make, model, year, registrationNumber, specifications, price));
        saveCarsToFile();
        System.out.println("CarService: Car added successfully");
        return true;
    }

    /**
     * Update car details
     * @param registrationNumber Registration number of car to update
     * @param newMake New make (null to keep current)
     * @param newModel New model (null to keep current)
     * @param newYear New year (null to keep current)
     * @param newSpecifications New specifications (null to keep current)
     * @param newPrice New price (null to keep current)
     * @return true if car updated, false if not found
     */
    public boolean updateCar(String registrationNumber, String newMake, String newModel, 
                           Integer newYear, String newSpecifications, Double newPrice) {
        System.out.println("CarService: Updating car with registration " + registrationNumber);
        Car car = cars.find(c -> c.getRegistrationNumber().equals(registrationNumber));
        if (car == null) {
            System.out.println("CarService: Car not found");
            return false;
        }

        if (newMake != null) car.setMake(newMake);
        if (newModel != null) car.setModel(newModel);
        if (newYear != null) car.setYear(newYear);
        if (newSpecifications != null) car.setSpecifications(newSpecifications);
        if (newPrice != null) car.setPrice(newPrice);

        saveCarsToFile();
        System.out.println("CarService: Car updated successfully");
        return true;
    }

    /**
     * Delete car
     * @param registrationNumber Registration number of car to delete
     * @return true if car deleted, false if not found
     */
    public boolean deleteCar(String registrationNumber) {
        System.out.println("CarService: Deleting car with registration " + registrationNumber);
        Car car = cars.find(c -> c.getRegistrationNumber().equals(registrationNumber));
        if (car == null) {
            System.out.println("CarService: Car not found");
            return false;
        }
        boolean result = cars.remove(car);
        if (result) {
            saveCarsToFile();
        }
        System.out.println("CarService: Car " + (result ? "deleted successfully" : "deletion failed"));
        return result;
    }

    /**
     * Get a car by its registration number
     * @param registrationNumber The registration number to search for
     * @return The car if found, null otherwise
     */
    public Car getCar(String registrationNumber) {
        System.out.println("CarService: Getting car with registration: " + registrationNumber);
        
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            System.out.println("CarService: Registration number is null or empty");
            return null;
        }

        Car[] allCars = getAllCars();
        for (Car car : allCars) {
            if (car.getRegistrationNumber().equals(registrationNumber)) {
                System.out.println("CarService: Found car: " + car);
                return car;
            }
        }

        System.out.println("CarService: Car not found with registration: " + registrationNumber);
        return null;
    }

    /**
     * Search cars by make and/or model
     * @param searchTerm Term to search in make and model
     * @return Array of matching cars, never null
     */
    public Car[] searchCars(String searchTerm) {
        System.out.println("CarService: Searching cars with term: " + searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            System.out.println("CarService: Search term is empty, returning all cars");
            return getAllCars();
        }

        LinkedList<Car> results = new LinkedList<>();
        Car[] allCars = cars.toArray();
        
        searchTerm = searchTerm.toLowerCase().trim();
        System.out.println("CarService: Normalized search term: " + searchTerm);
        
        for (Car car : allCars) {
            if (car.getMake().toLowerCase().contains(searchTerm) || 
                car.getModel().toLowerCase().contains(searchTerm)) {
                results.add(car);
            }
        }
        
        Car[] resultArray = results.toArray();
        System.out.println("CarService: Found " + resultArray.length + " matching cars");
        return resultArray;
    }

    /**
     * Get all cars
     * @return Array of all cars
     */
    public Car[] getAllCars() {
        System.out.println("CarService: Getting all cars");
        Car[] result = cars.toArray();
        System.out.println("CarService: Returning " + (result != null ? result.length : 0) + " cars");
        if (result != null && result.length > 0) {
            System.out.println("CarService: First car in list: " + result[0]);
        }
        return result;
    }
} 