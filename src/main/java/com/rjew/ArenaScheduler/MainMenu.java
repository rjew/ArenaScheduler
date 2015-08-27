package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MainMenu {

    final static Logger logger = Logger.getLogger(MainMenu.class);

    public static void displayArenaSchedulerMenu() {
        int option;

        try (Scanner keyboard = new Scanner(System.in)) {

            System.out.println("Welcome to the Arena Scheduler Program!\n");

            /* Print the Menu */
            do {
                displayMainMenu();

                option = getArenaSchedulerOption(keyboard);

            } while (option != 4);

        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
    }

    public static void displayMainMenu() {
        System.out.println("What would you like to do?\n" +
                "(1) Search Catalog\n" +
                "(2) Modify/View your custom schedules\n" +
                "(3) View Full Announcer\n" +
                "(4) Quit\n" +
                "Pick an option (1-4) and press ENTER.");
    }

    public static int getArenaSchedulerOption(Scanner keyboard) {
        int option = 0;

        try {
            option = keyboard.nextInt();
        } catch (InputMismatchException ex) {
            //ignore exception, prompt user again for input if input is incorrect
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
        keyboard.nextLine();//clear the buffer

        switch (option) {
            case 1:
                SearchCatalog.displaySearchCatalog(keyboard);
                break;
            case 2:
                CustomScheduleManagerMenu.accessCustomSchedules(keyboard);
                break;
            case 3:
                FullAnnouncer.viewFullAnnouncer(keyboard);
                break;
            case 4:
                break;
            default:
                System.out.println("\nWRONG OPTION!\n");
        }

        return option;
    }
}
