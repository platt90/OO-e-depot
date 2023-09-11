package ljmu.depots;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.function.Function;

import platform.Sys;

public class Depot {

    private Integer depotNumber;
    private Driver user;
    private ArrayList<Driver> drivers = new ArrayList<Driver>();
    private ArrayList<Truck> trucks = new ArrayList<Truck>();
    private ArrayList<Tanker> tankers = new ArrayList<Tanker>();
    private ArrayList<WorkSchedule> workSchedules = new ArrayList<WorkSchedule>();

    Scanner userInput = new Scanner(System.in);

    /**
     * Constructor
     */
    public Depot(Integer depotNumber) {
        this.depotNumber = depotNumber;

        String line = "";

        /**
         * Read all CSV Files and add data to the relevant Array List
         */
        try {
            // Manager CSV
            BufferedReader managersBr = new BufferedReader(new FileReader("managers.csv"));
            managersBr.readLine(); // Skip first line in CSV file
            while ((line = managersBr.readLine()) != null) {
                String[] manager = line.split(","); // use comma as separator
                int depotNum = Integer.parseInt(manager[2]);
                drivers.add(new Manager(manager[0], manager[1], depotNum));
            }
            // Driver CSV
            BufferedReader driversBr = new BufferedReader(new FileReader("drivers.csv"));
            driversBr.readLine(); // Skip first line in CSV file
            while ((line = driversBr.readLine()) != null) {
                String[] driver = line.split(","); // use comma as separator
                int depotNum = Integer.parseInt(driver[2]);
                drivers.add(new Driver(driver[0], driver[1], depotNum));
            }
            // Trucks CSV
            BufferedReader trucksBr = new BufferedReader(new FileReader("trucks.csv"));
            while ((line = trucksBr.readLine()) != null) {
                String[] truck = line.split(","); // use comma as separator
                int weight = Integer.parseInt(truck[2]);
                int cargoCapacity = Integer.parseInt(truck[4]);
                int assignedDepot = Integer.parseInt(truck[5]);
                LocalDateTime moveDate = LocalDateTime.parse(truck[6]);
                trucks.add(new Truck(truck[0], truck[1], weight, truck[3], cargoCapacity, assignedDepot, moveDate));
            }
            // Tankers CSV
            BufferedReader tankersBr = new BufferedReader(new FileReader("tankers.csv"));
            while ((line = tankersBr.readLine()) != null) {
                String[] tanker = line.split(","); // use comma as separator
                int weight = Integer.parseInt(tanker[2]);
                int liquidCapacity = Integer.parseInt(tanker[4]);
                int assignedDepot = Integer.parseInt(tanker[6]);
                LocalDateTime moveDate = LocalDateTime.parse(tanker[7]);
                tankers.add(new Tanker(tanker[0], tanker[1], weight, tanker[3], liquidCapacity, tanker[5],
                        assignedDepot, moveDate));
            }
            // WorkSchedules CSV
            BufferedReader workSchedulesBr = new BufferedReader(new FileReader("workschedules.csv"));
            while ((line = workSchedulesBr.readLine()) != null) {
                String[] workSchedule = line.split(","); // use comma as separator
                LocalDateTime startDate = LocalDateTime.parse(workSchedule[1]);
                LocalDateTime endDate = LocalDateTime.parse(workSchedule[2]);
                Driver driver = getDriver(workSchedule[3]);
                Vehicle vehicle = getVehicle(workSchedule[4]);
                JobState jobState = JobState.valueOf(workSchedule[5]);
                workSchedules.add(new WorkSchedule(workSchedule[0], startDate, endDate, driver, vehicle, jobState));
            }
            // Close Buffered Readers
            driversBr.close();
            managersBr.close();
            trucksBr.close();
            tankersBr.close();
            workSchedulesBr.close();
            // Catch exception
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write data to all CSV Files from the ArrayLists
     */
    public void csvWriter() {
        try {
            // Driver CSV
            appendUniqueDataToCSV(drivers, "drivers.csv", (Driver driver) -> {
                if (!(driver instanceof Manager)) {
                    return driver.getUserName() + "," + driver.getPassWord() + "," + driver.getDepotNumber();
                }
                return null; // Skip managers
            });

            // Manager CSV
            appendUniqueDataToCSV(drivers, "managers.csv", (Driver driver) -> {
                if (driver instanceof Manager) {
                    return driver.getUserName() + "," + driver.getPassWord() + "," + driver.getDepotNumber();
                }
                return null; // Skip non-managers
            });

            // WorkSchedules CSV
            writeDataToCSV(workSchedules, "workschedules.csv", (WorkSchedule workSchedule) -> {
                return workSchedule.getClient() + "," + workSchedule.getStartDate() + ","
                        + workSchedule.getEndDate() + "," + workSchedule.getDriver().getUserName() + ","
                        + workSchedule.getVehicle().getRegNumber() + "," + workSchedule.getJobState();
            });

            // Trucks CSV
            appendUniqueDataToCSV(trucks, "trucks.csv", (Truck truck) -> {
                return truck.getMake() + "," + truck.getModel() + "," + truck.getWeight() + ","
                        + truck.getRegNumber() + "," + truck.getCargoCapacity() + "," + truck.getAssignedDepot() + ","
                        + truck.getMoveDate();
            });

            // Tankers CSV
            appendUniqueDataToCSV(tankers, "tankers.csv", (Tanker tanker) -> {
                return tanker.getMake() + "," + tanker.getModel() + "," + tanker.getWeight() + ","
                        + tanker.getRegNumber() + "," + tanker.getLiquidCapacity() + "," + tanker.getLiquidType() + ","
                        + tanker.getAssignedDepot() + "," + tanker.getMoveDate();
            });

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace(); // Handle exceptions properly, don't leave them empty.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generic method to write data to CSV
    private <T> void writeDataToCSV(List<T> dataList, String fileName, Function<T, String> dataFormatter)
            throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"))) {
            for (T data : dataList) {
                String line = dataFormatter.apply(data);
                if (line != null) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        }
    }

    // Generic method to append unique data to CSV
    private <T> void appendUniqueDataToCSV(List<T> dataList, String fileName, Function<T, String> dataFormatter)
            throws IOException {
        Set<String> existingData = readExistingData(fileName);

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8"))) {
            for (T data : dataList) {
                String line = dataFormatter.apply(data);
                if (line != null && !existingData.contains(line)) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        }
    }

    // Method to read existing data from a CSV file
    private Set<String> readExistingData(String fileName) throws IOException {
        Set<String> existingData = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                existingData.add(line);
            }
        }
        return existingData;
    }


    /**
     * Login
     */
    public void logOn() {
        Boolean userAuthenticate = false;

        do {
            System.out.print("\nEnter your username: ");
            String userName = userInput.nextLine();
            System.out.print("Enter your password: ");
            String passWord = userInput.nextLine();
            // Loop through Drivers ArryList to check user input for validation
            for (Driver driver : drivers) {
                if (driver.getUserName().equals(userName)) {
                    if (driver.checkPassWord(passWord)) {
                        userAuthenticate = true;
                        user = driver;
                        System.out.println("\nWelcome back " + user.getUserName());
                    }
                }
            }
            if (!userAuthenticate) {
                System.out.println("\nIncorrect Login Details - Try Again");
            }
        } while (!userAuthenticate);
        depotMenu();
    }

    /**
     * Method to Display the full Vehicle List - Manager use only
     */
    public void viewVehicleList() {
        String truckList = trucks.toString().replace(",", "").replace("[", "").replace("]", "").trim();
        String tankerList = tankers.toString().replace(",", "").replace("[", "").replace("]", "").trim();
        System.out.print("\n" + truckList + "\n" + tankerList + "\n");
    }

    /**
     * Method to Display only available vehicles with no active work schedule Used
     * for setting a work schedule/moving vehicle
     */
    public void viewAvailableVehicles() {
        String truckList = "";
        String tankerList = "";
        System.out.println("\nCurrent Available Vehicles\n");
        for (Vehicle truck : trucks) {
            if (truck.isAvailable(truck)) {
                truckList = truck.toString().replace(",", "").replace("[", "").replace("]", "").trim();
                System.out.print(truckList + "\n");
            }
        }
        for (Vehicle tanker : tankers) {
            if (tanker.isAvailable(tanker)) {
                tankerList = tanker.toString().replace(",", "").replace("[", "").replace("]", "").trim();
                System.out.print(tankerList + "\n");
            }
        }
    }

    /**
     * Method to Display only available drivers with no active work schedule Used
     * for setting a work schedule
     */
    public void viewAvailableDrivers() {
        String driverList = "";
        System.out.println("\nCurrent Available Drivers\n");
        for (Driver driver : drivers) {
            if (driver.isAvailable(driver)) {
                driverList = driver.toString().replace(",", "").replace("[", "").replace("]", "").trim();
                System.out.print(driverList + "\n");
            }
        }
    }

    /**
     * Method to Display the full list of drivers
     */
    public void viewDriverList() {
        String driverList = drivers.toString().replace(",", "").replace("[", "").replace("]", "").trim();
        System.out.print("\n" + driverList + "\n");
    }

    /**
     * Method to Display the users own work schedule Used for drivers to view only
     * their work schedule
     */
    public void viewUserWorkSchedule() {
        String workSchedule = "";
        for (WorkSchedule workschedule : workSchedules) {
            if (workschedule.getDriver().getUserName().contains(user.getUserName())) {
                workSchedule = workschedule.toString().replace(",", "").replace("[", "").replace("]", "").trim();
            }
        }
        if (!workSchedule.equals("")) {
            System.out.println("\n" + workSchedule + "\n");
        } else {
            System.out.println("\nNo Current Work Schedule\n");
        }
    }

    /**
     * Method to Display the full Work Schedules List Used for managers to view all
     * work schedules
     */
    public void viewFullWorkSchedule() {
        String workSchedule = workSchedules.toString().replace(",", "").replace("[", "").replace("]", "").trim();
        System.out.print("\n" + workSchedule + "\n");
    }

    /**
     * Method to fill the Schedules on Vehicle and Driver Class Used for validation
     * purposes with isAvailable() methods
     */
    public void setSchedules() {
        for (Vehicle tanker : tankers) {
            for (WorkSchedule workschedule : workSchedules) {
                tanker.setSchedule(workschedule);
            }
        }
        for (Vehicle truck : trucks) {
            for (WorkSchedule workschedule : workSchedules) {
                truck.setSchedule(workschedule);
            }
        }
        for (Driver driver : drivers) {
            for (WorkSchedule workschedule : workSchedules) {
                driver.setSchedule(workschedule);
            }
        }
    }

    /**
     * Method to scan the driver List and return the selected driver Used when
     * setting up a work schedule and selecting a driver
     *
     * @return driver
     */
    public Driver getDriver(String driverName) {
        for (Driver driver : drivers) {
            if (driver.getUserName().equals(driverName)) {
                return driver;
            }
        }
        return null;
    }

    /**
     * Method used to scan the vehicle Lists and return the selected vehicle Used
     * when setting up a work schedule and moving a vehicle to another depot
     *
     * @return vehicle
     */
    public Vehicle getVehicle(String regNo) {
        for (Vehicle vehicle : trucks) {
            if (vehicle.getRegNumber().equals(regNo)) {
                return vehicle;
            }
        }
        for (Vehicle vehicle : tankers) {
            if (vehicle.getRegNumber().equals(regNo)) {
                return vehicle;
            }
        }
        return null;
    }

    /**
     * Method to add the new work schedule to the work schedules ArrayList
     *
     * @param workSchedule
     */
    public void setupWorkSchedule(WorkSchedule workSchedule) {
        workSchedules.add(workSchedule);
    }

    /**
     * Method to add a new driver
     */
    public void setupDriver() {
        System.out.print("Enter New Driver Name: ");
        String userName = userInput.nextLine();
        System.out.print("Enter New Driver Password: ");
        String passWord = userInput.nextLine();
        Driver driver = new Driver(userName, passWord, depotNumber);
        addNewDriver(driver);
        System.out.println("Driver Succesfully Added");
    }

    /**
     * Method to add a new vehicle
     */
    public void setupVehicle() {
        Boolean validInput = false, validInt = false;
        String type, make, model, regNo, liquidType;
        Integer weight = 0, liquidCapacity = 0, cargoCapacity = 0;
        LocalDateTime moveDate = LocalDateTime.now();

        do {
            System.out.print("\n--Vehicle Type--\n\n1 - Truck\n2 - Tanker\n");
            System.out.print("\nEnter Vehicle Type: ");
            type = userInput.nextLine();
            if (type.equals("1") || type.equals("2")) {
                validInput = true;
            }
        } while (!validInput);

        System.out.print("Enter Vehicle Make: ");
        make = userInput.nextLine();
        System.out.print("Enter Vehicle Model: ");
        model = userInput.nextLine();
        do {
            System.out.print("Enter Vehicle Weight: ");
            String sWeight = userInput.nextLine();
            /*
             * Try and catch is used here to ensure the program doesn't crash with
             * inappropriate user input
             */
            try {
                weight = Integer.parseInt(sWeight);
                validInt = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input - Try Again");
            }
        } while (!validInt);

        System.out.print("Enter Vehicle Reg No: ");
        regNo = userInput.nextLine();
        validInt = false;
        /*
         * If the vehicle being added is a truck
         */
        if (type.equals("1")) {
            do {
                System.out.print("Enter Cargo Capacity: ");
                String sCargoCapacity = userInput.nextLine();
                try {
                    cargoCapacity = Integer.parseInt(sCargoCapacity);
                    validInt = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input - Try Again");
                }
            } while (!validInt);

            Truck truck = new Truck(make, model, weight, regNo, cargoCapacity, depotNumber, moveDate);
            addNewTruck(truck);
            /*
             * If the vehicle being added is a tanker
             */
        } else {
            do {
                System.out.print("Enter Liquid Capacity: ");
                String liquidCapacity2 = userInput.nextLine();
                try {
                    liquidCapacity = Integer.parseInt(liquidCapacity2);
                    validInt = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input - Try Again");
                }
            } while (!validInt);
            System.out.print("Enter Liquid Type: ");
            liquidType = userInput.nextLine();
            ;
            Tanker tanker = new Tanker(make, model, weight, regNo, liquidCapacity, liquidType, depotNumber, moveDate);
            addNewTanker(tanker);
        }
        System.out.println("Vehicle Succesfully Added");
    }

    /**
     * Method to add a new driver to the driver ArrayList
     *
     * @param driver
     */
    public void addNewDriver(Driver driver) {
        drivers.add(driver);
    }

    /**
     * Method to add a new truck to the truck ArrayList
     */
    public void addNewTruck(Truck truck) {
        trucks.add(truck);
    }

    /**
     * Method to add a new tanker to the tanker ArrayList
     */
    public void addNewTanker(Tanker tanker) {
        tankers.add(tanker);
    }

    /**
     * Method to set up a new work schedule
     */
    public void setupWorkSchedule() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Boolean validStartDate = false, validEndDate = false, validDriver = false, validVehicle = false;
        String driverName, regNo, inputStartDate, inputEndDate;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        System.out.print("\nEnter Client Name: ");
        String client = userInput.nextLine();

        do {
            System.out.print("Enter Job Start Date & Time (dd/MM/yyyy HH:mm): ");
            inputStartDate = userInput.nextLine();
            /*
             * Try and catch is used here to ensure the program doesn't crash with
             * inappropriate user input
             */
            try {
                startDate = LocalDateTime.parse(inputStartDate, df);
                if (startDate.isAfter(LocalDateTime.now())) { // make sure the date is not in the past
                    validStartDate = true;
                } else {
                    System.out.println("\nJob Start Date Cannot Be In The Past\n");
                }
            } catch (DateTimeParseException e) {
                // Throw invalid date message
                System.out.println("\nInvalid Date - Try Again\n");
            }
        } while (!validStartDate);

        do {
            System.out.print("Enter Job End Date & Time (dd/MM/yyyy HH:mm): ");
            inputEndDate = userInput.nextLine();
            /*
             * Try and catch is used here to ensure the program doesn't crash with
             * inappropriate user input
             */
            try {
                endDate = LocalDateTime.parse(inputEndDate, df);
                if (endDate.isAfter(LocalDateTime.now()) && endDate.isAfter(startDate)) { // make sure the date is
                    // not in the past or
                    // before the job start
                    // date
                    validEndDate = true;
                } else {
                    System.out.println("\nJob End Date Cannot Be In The Past And Must Be After Start Date\n");
                }
            } catch (DateTimeParseException e) {
                // Throw invalid date message
                System.out.println("\nInvalid Date - Try Again\n");
            }
        } while (!validEndDate);

        viewAvailableDrivers(); // display the available drivers
        do {
            System.out.print("\nEnter Driver Name: ");
            driverName = userInput.nextLine();
            for (Driver driver : drivers) {
                if (driver.getUserName().equals(driverName)) {
                    if (driver.isAvailable(driver)) {
                        validDriver = true;
                    } else {
                        System.out.println("\nDriver Unavailable Due To Current Work Schedule");
                    }
                }
            }
            if (!validDriver) {
                System.out.println("Invalid Entry - Try Again\n");
            }
        } while (!validDriver);

        viewAvailableVehicles(); // display the available vehicles
        do {
            System.out.print("\nEnter Vehicle RegNo: ");
            regNo = userInput.nextLine();
            for (Vehicle vehicle : trucks) {
                if (vehicle.getRegNumber().equals(regNo)) {
                    if (vehicle.isAvailable(vehicle)) {
                        validVehicle = true;
                    } else {
                        System.out.println("\nVehicle Unavailable Due To Current Work Schedule");
                    }
                }
            }
            for (Vehicle vehicle : tankers) {
                if (vehicle.getRegNumber().equals(regNo)) {
                    if (vehicle.isAvailable(vehicle)) {
                        validVehicle = true;
                    } else {
                        System.out.println("\nVehicle Unavailable Due To Current Work Schedule");
                    }
                }
            }
            if (!validVehicle) {
                System.out.println("Invalid Entry - Try Again\n");
            }
        } while (!validVehicle);
        Vehicle vehicle = getVehicle(regNo); // set vehicle to what user has chosen to be added to work schedule
        Driver driver = getDriver(driverName); // set driver to what user has chosen to be added to work schedule
        JobState jobState = JobState.PENDING; // by default set the job state to pending
        WorkSchedule workSchedule = new WorkSchedule(client, startDate, endDate, driver, vehicle, jobState);
        setupWorkSchedule(workSchedule); // add new workSchedule object to the ArrayList
        vehicle.setSchedule(workSchedule); // update the vehicle class ArrayList
        driver.setSchedule(workSchedule);// update the driver class ArrayList
        System.out.println("\nJob Succesfully Added To Work Schedule\n");
    }

    /**
     * Method to move a vehicle to another depot
     */
    public void moveVehicle() {

        Boolean validMoveDate = false, validVehicle = false, validDepot = false;
        String regNo, inputMoveDate, inputDepot;
        Integer intDepot = 0;
        LocalDateTime moveDate = null;

        viewAvailableVehicles(); // Display available vehicles that can be moved
        do {
            System.out.print("\nEnter Vehicle RegNo: ");
            regNo = userInput.nextLine();
            for (Vehicle vehicle : trucks) {
                if (vehicle.getRegNumber().equals(regNo)) {
                    if (vehicle.isAvailable(vehicle)) {
                        validVehicle = true;
                    } else {
                        System.out.println("\nVehicle Unavailable Due To Current Work Schedule");
                    }
                }
            }
            for (Vehicle vehicle : tankers) {
                if (vehicle.getRegNumber().equals(regNo)) {
                    if (vehicle.isAvailable(vehicle)) {
                        validVehicle = true;
                    } else {
                        System.out.println("\nVehicle Unavailable Due To Current Work Schedule");
                    }
                }
            }
            if (!validVehicle) {
                System.out.println("Invalid Entry - Try Again\n");
            }
        } while (!validVehicle);

        Vehicle vehicle = getVehicle(regNo);

        do {
            System.out.print("Enter Vehicle Move Date & Time (dd/MM/yyyy HH:mm): ");
            inputMoveDate = userInput.nextLine();
            try {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                moveDate = LocalDateTime.parse(inputMoveDate, df);
                if (moveDate.isAfter(LocalDateTime.now())) {
                    validMoveDate = true;
                } else {
                    System.out.println("\nDate Cannot Be In The Past\n");
                }
            } catch (DateTimeParseException e) {
                // Throw invalid date message
                System.out.println("\nInvalid Date - Try Again\n");
            }
        } while (!validMoveDate);

        do {
            System.out.print("\n--Select New Depot--\n\n1 - Liverpool\n2 - Manchester\n3 - Leeds\n");
            System.out.print("\nEnter Depot Number: ");
            inputDepot = userInput.nextLine();
            if (inputDepot.equals("1") || inputDepot.equals("2") || inputDepot.equals("3")) {
                intDepot = Integer.parseInt(inputDepot);
                if (!vehicle.getAssignedDepot().equals(intDepot)) {
                    validDepot = true;
                } else {
                    System.out.println("\nSelected Vehicle Already Assigned to Depot Number " + inputDepot);
                }

            } else {
                System.out.println("\nInvalid Entry - Try Again\n");
            }
        } while (!validDepot);
        vehicle.setAssignedDepot(intDepot);
        vehicle.setMoveDate(moveDate);
        System.out.println("\nVehicle Move Succesfully Scheduled\n");
    }

    /**
     * Getters & Setters
     */
    public int getDepotNumber() {
        return depotNumber;
    }

    public void setDepotNumber(Integer depotNumber) {
        this.depotNumber = depotNumber;
    }

    /**
     * Depot Menu
     */
    public void depotMenu() {

        String choice = null;
        String depotName = "";

        if (depotNumber.equals(1)) {
            depotName = "Liverpool";
        } else if (depotNumber.equals(2)) {
            depotName = "Manchester";
        } else if (depotNumber.equals(3)) {
            depotName = "Leeds";
        }

        if (user instanceof Manager) {
            do {
                System.out.print("\n--DEPOT MENU--\n\n" + depotName
                        + " Depot\n\n1 - View Work Schedule\n2 - View Vehicle List\n3 - View Driver List\n4 - Move Vehicle\n5 - Setup Work Schedule\n6 - Setup New Vehicle\n7 - Setup New Driver\n8 - Logout\n\nPick: ");
                choice = userInput.nextLine().toUpperCase();
                switch (choice.toUpperCase()) {
                    case "1" -> viewFullWorkSchedule();
                    case "2" -> viewVehicleList();
                    case "3" -> viewDriverList();
                    case "4" -> moveVehicle();
                    case "5" -> setupWorkSchedule();
                    case "6" -> setupVehicle();
                    case "7" -> setupDriver();
                    case "8" -> csvWriter();
                    case "9" -> viewAvailableDrivers();
                    default -> System.out.println("\nInvalid Choice");
                }
            } while (!choice.equals("8"));
        } else do {
            System.out.print("\n--DEPOT MENU--\n\n" + depotName
                    + " Depot\n\n1 - View Work Schedule\n2 - Logout\n\nPick: ");
            choice = userInput.nextLine().toUpperCase();
            switch (choice.toUpperCase()) {
                case "1" -> viewUserWorkSchedule();
                case "2" -> csvWriter();
                default -> System.out.println("\nInvalid Choice");
            }
        } while (!choice.equals("2"));
        System.out.println("\nSuccessfully Logged Out");
        new Sys().getDepot();
    }
}




