package ljmu.depots;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Truck extends Vehicle {

    private Integer cargoCapacity;

    /**
     * Constructor
     */
    public Truck(String make, String model, Integer weight, String regNumber, Integer cargoCapacity, Integer assignedDepot, LocalDateTime moveDate) {
        super(make, model, weight, regNumber, assignedDepot, moveDate);
        this.cargoCapacity = cargoCapacity;
    }

    /**
     * Getters & Setters
     */
    public Integer getCargoCapacity() {
        return cargoCapacity;
    }

    public void setCargoCapacity(Integer cargoCapacity) {
        this.cargoCapacity = cargoCapacity;
    }

    /**
     * String Override
     */
    @Override
    public String toString() {
        return "\n\u2022\t" + this.getClass().getSimpleName() + "\t>> Make: " + make + ", Model: " + model + ", Weight: " + weight + ", Reg Number: " + regNumber + ", Cargo Capacity: " + cargoCapacity + ", Assigned Depot: " + assignedDepot + ", Move Date & Time: " + moveDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }


}
