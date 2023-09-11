package platform;

import java.util.*;
import ljmu.depots.Depot;

public class Sys {

    private Scanner userInput = new Scanner(System.in);
    private static int depotNumber;

    public Sys() {

    }

    /**
     * User Menu
     */
    public void getDepot() {
        String choice;

        do {
            System.out.print("\n--Enter Depot Number\n\n1 - Liverpool\n2 - Manchester\n3 - Leeds\nQ - Quit\n\nDepot >: ");
            choice = userInput.nextLine().toUpperCase();
            if (choice.equals("1") || choice.equals("2") || choice.equals("3")) {
                depotNumber = Integer.parseInt(choice);
                Depot depot = new Depot(depotNumber);
                depot.logOn();
            } else if (choice.equals("Q")) {
                System.out.println("\n--Goodbye--\n");
                System.exit(0);
                break;
            } else {
                System.out.println("\nInvalid Choice - Try Again");
            }
        } while (!choice.equals("Q"));
    }
}
