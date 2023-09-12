package ljmu.depots;

import java.util.ArrayList;

public class Driver {

    protected String userName;
    protected String passWord;
    protected Integer depotNumber;

    private ArrayList<WorkSchedule> driverSchedule = new ArrayList<WorkSchedule>();

    /**
     * Constructor
     */

    public Driver(String userName, String passWord, Integer depotNumber) {
        this.userName = userName;
        this.passWord = passWord;
        this.depotNumber = depotNumber;
    }

    public void importWorkSchedules(ArrayList<WorkSchedule> workSchedules) {
        // Add work schedules from the CSV files to the driver's schedule
        this.driverSchedule.addAll(workSchedules);
    }

    public boolean isAvailable(Driver driver) {
        for (WorkSchedule driverSchedule : driverSchedule) {
            driverSchedule.updateJobState();
            if (driverSchedule.getDriver().equals(driver)) {
                if (!driverSchedule.getJobState().equals(JobState.ARCHIVED)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkPassWord(String passWord) {
        if (this.passWord.equals(passWord)) {
            return true;
        }
        return false;
    }

    public void setSchedule(WorkSchedule workSchedule) {
        driverSchedule.add(workSchedule);
    }

    /**
     * Getters & Setters
     */
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public Integer getDepotNumber() {
        return depotNumber;
    }

    public void setDepotNumber(Integer depotNumber) {
        this.depotNumber = depotNumber;
    }

    /**
     * String Override
     */
    @Override
    public String toString() {
        return "\n\u2022\t" + this.getClass().getSimpleName() + "\t>> Name: " + userName + "\tDepotNumber:" + depotNumber;
    }

}
