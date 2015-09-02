package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

final class CustomScheduleManagerMenu {

    private final static Logger logger = Logger.getLogger(CustomScheduleManagerMenu.class);

    /**
     * To prevent instantiation
     */
    private CustomScheduleManagerMenu() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * The menu for customizing the schedules
     * @param keyboard For user input
     */
    public static void accessCustomSchedules(Scanner keyboard) {
        int menuOption;

        do {
            displayCustomSchedulesMenu();

            menuOption = getCustomSchedulesMenuOption(keyboard);

        } while (menuOption != 8);
    }

    /**
     * Display the menu for the custom schedule manager
     */
    private static void displayCustomSchedulesMenu() {
        System.out.println("\nWhat would you like to do?\n" +
                "(1) Add a course to a schedule\n" +
                "(2) Delete a course from a schedule\n" +
                "(3) Create a new schedule\n" +
                "(4) Delete a schedule\n" +
                "(5) View one of your schedules\n" +
                "(6) Rename a schedule\n" +
                "(7) Duplicate one of your schedules\n" +
                "(8) Quit");
    }

    /**
     * Get user input for the custom schedule menu and process it with the appropriate option
     * @param keyboard For user input
     * @return An int holding the user's menu option
     */
    private static int getCustomSchedulesMenuOption(Scanner keyboard) {
        int menuOption = 0;
        int scheduleOption;
        ArrayList<String> tableNamesArrayList = new ArrayList<>();

        try {
            menuOption = keyboard.nextInt();
        } catch (InputMismatchException ex) {
            //ignore exception, prompt user again for input if input is incorrect
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
        keyboard.nextLine();//clear keyboard buffer

        switch (menuOption) {
            case 1:
                try {
                    SearchCatalog.displaySearchCatalog(keyboard);
                } catch (Exception ex) {
                    logger.error(ex);
                    System.err.println("ERROR: " + ex.getMessage());
                }
                break;
            case 2:
                scheduleOption = CustomScheduleManagerSelection.displayScheduleOptions(keyboard, tableNamesArrayList, "select");
                if (scheduleOption != 0) {
                    CustomScheduleManagerDeleteClass.deleteClass(keyboard, tableNamesArrayList.get(scheduleOption - 1));
                }
                break;
            case 3:
                try {
                    CustomScheduleManagerCreateSchedule.createSchedule(keyboard);
                } catch (Exception ex) {
                    logger.error(ex);
                    System.err.println("ERROR: " + ex.getMessage());
                }
                break;
            case 4:
                scheduleOption = CustomScheduleManagerSelection.displayScheduleOptions(keyboard, tableNamesArrayList, "delete");
                if (scheduleOption != 0) {
                    CustomScheduleManagerDeleteSchedule.deleteSchedule(tableNamesArrayList.get(scheduleOption - 1));
                }
                break;
            case 5:
                scheduleOption = CustomScheduleManagerSelection.displayScheduleOptions(keyboard, tableNamesArrayList, "view");
                if (scheduleOption != 0) {
                    CustomScheduleManagerViewSchedule.viewSchedule(tableNamesArrayList.get(scheduleOption - 1));
                }
                break;
            case 6:
                scheduleOption = CustomScheduleManagerSelection.displayScheduleOptions(keyboard, tableNamesArrayList, "rename");
                if (scheduleOption != 0) {
                    CustomScheduleManagerRename.renameSchedule(keyboard, tableNamesArrayList.get(scheduleOption - 1));
                }
                break;
            case 7:
                scheduleOption = CustomScheduleManagerSelection.displayScheduleOptions(keyboard, tableNamesArrayList, "copy");
                if (scheduleOption != 0) {
                    CustomScheduleManagerDuplicate.duplicateSchedule(keyboard, tableNamesArrayList.get(scheduleOption - 1));
                }
                break;
            case 8:
                System.out.println();
                break;
            default:
                System.out.println("\nWRONG OPTION!");
        }

        return menuOption;
    }
}
