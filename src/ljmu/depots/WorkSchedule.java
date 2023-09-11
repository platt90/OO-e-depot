package ljmu.depots;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WorkSchedule {

    private String client;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Driver driver;
    private Vehicle vehicle;
    private JobState jobState;

    /**
     * Constructor
     */
    public WorkSchedule(String client, LocalDateTime startDate, LocalDateTime endDate, Driver driver, Vehicle vehicle, JobState jobState) {
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
        this.driver = driver;
        this.vehicle = vehicle;
        this.jobState = jobState;
    }

    /**
     * Update job state
     */
    public void updateJobState() {
        if(!jobState.equals(jobState.ACTIVE) && startDate.isBefore(LocalDateTime.now()) && endDate.isAfter(LocalDateTime.now())) {
            setJobState(JobState.ACTIVE);
        } else if (!jobState.equals(JobState.ARCHIVED) && endDate.isBefore(LocalDateTime.now())) {
            setJobState(JobState.ARCHIVED);
        } else if (!jobState.equals(JobState.PENDING) && startDate.isAfter(LocalDateTime.now())) {
            setJobState(JobState.PENDING);
        }
    }

    /**
     * Getters & Setters
     */
    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public JobState getJobState() {
        return jobState;
    }

    public void setJobState(JobState jobState) {
        this.jobState = jobState;
    }

    /**
     * String Override
     */
    @Override
    public String toString() {
        updateJobState();
        return "\n\u2022\t" + this.getClass().getSimpleName() + " >> Client: " + client + "\tJob Start Date & Time: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                ", Job End Date & Time: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ", Driver: " + driver.getUserName() + ", Vehicle Reg Number: " + vehicle.getRegNumber() + ", Job State: " + jobState;
    }
}
