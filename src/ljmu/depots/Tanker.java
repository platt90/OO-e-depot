package ljmu.depots;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Tanker extends Vehicle {

    private Integer liquidCapacity;
    private String liquidType;

    /**
     * Constructor
     */
    public Tanker(String make, String model, Integer weight, String regNumber, Integer liquidCapacity, String liquidType, Integer assignedDepot, LocalDateTime moveDate) {
        super(make, model, weight, regNumber, assignedDepot, moveDate);
        this.liquidCapacity = liquidCapacity;
        this.liquidType = liquidType;
    }

    /**
     * Getters & Setters
     */
    public Integer getLiquidCapacity() {
        return liquidCapacity;
    }

    public String getLiquidType() {
        return liquidType;
    }

    public void setLiquidCapacity(Integer liquidCapacity) {
        this.liquidCapacity = liquidCapacity;
    }

    public void setLiquidType(String liquidType) {
        this.liquidType = liquidType;
    }

    /**
     * String Override
     */
    @Override
    public String toString() {
        return "\n\u2022\t" + this.getClass().getSimpleName() + "\t>> Make: " + make + ", Model: " + model + ", Weight: " + weight + ", Reg Number: " + regNumber + ", Liquid Capacity: " + liquidCapacity + ", Liquid Type: " + liquidType + ", Assigned Depot: " + assignedDepot + ", Move Date & Time: " + moveDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
