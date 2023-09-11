package ljmu.depots;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Vehicle {

    protected String make;
    protected String model;
    protected Integer weight;
    protected String regNumber;
    protected Integer assignedDepot;
    protected LocalDateTime moveDate;

    private ArrayList<WorkSchedule> vehicleSchedule = new ArrayList<WorkSchedule>();

    /**
     * Constructor
     */
    public Vehicle(String make, String model, Integer weight, String regNumber, Integer assignedDepot, LocalDateTime moveDate) {
        this.make = make;
        this.model = model;
        this.weight = weight;
        this.regNumber = regNumber;
        this.assignedDepot = assignedDepot;
        this.moveDate = moveDate;
    }

    /**
     * Determine whether Vehicle is available
     */
    public boolean isAvailable(Vehicle vehicle) {
        for(WorkSchedule vehicleSchedule: vehicleSchedule) {
            vehicleSchedule.updateJobState(); // ensures all job states are upto date
            if (vehicleSchedule.getVehicle().equals(vehicle)) {
                if (!vehicleSchedule.getJobState().equals(JobState.ARCHIVED))
                    return false;
            }
        }
        return true;
    }

    /**
     * Fills the vehicleShecule ArrayList with vehicle work schedules
     * The ArrayList is then used to determine if a vehicle is available for a new job or to move depots
     */
    public void setSchedule(WorkSchedule workSchedule) {
        vehicleSchedule.add(workSchedule);
    }

    /**
     * Getters & Setters
     */
    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public Integer getAssignedDepot() {
        return assignedDepot;
    }

    public void setAssignedDepot(Integer assignedDepot) {
        this.assignedDepot = assignedDepot;
    }

    public LocalDateTime getMoveDate() {
        return moveDate;
    }

    public void setMoveDate(LocalDateTime moveDate) {
        this.moveDate = moveDate;
    }
}
