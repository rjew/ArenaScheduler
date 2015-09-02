package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

final class CustomScheduleManagerSelection {

    private final static Logger logger = Logger.getLogger(CustomScheduleManagerSelection.class);

    /**
     * To prevent instantiation
     */
    private CustomScheduleManagerSelection() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Responsible for displaying the custom schedules that the user can select from
     * @param keyboard For user input
     * @param tableNamesArrayList An ArrayList holding the custom schedule names
     * @param displayOption A String to modify the output depending on the custom schedule manager option the user chooses
     * @return An int containing the schedule option the user chooses, for the index of the tableNamesArrayList
     */
    public static int displayScheduleOptions(Scanner keyboard, ArrayList<String> tableNamesArrayList,
                                             String displayOption) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:Custom_Schedules;create=true"; //For db Connection

        int scheduleOption = 0;

        tableNamesArrayList.clear();

        try (Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL)) {

            DatabaseMetaData customScheduleDBMeta = customScheduleConn.getMetaData();

            try (ResultSet customScheduleDBMetaTables =
                         customScheduleDBMeta.getTables(null, null, "%", new String[]{"TABLE"})) {

                if (customScheduleDBMetaTables.next()) {

                    displaySchedules(displayOption, tableNamesArrayList, customScheduleDBMetaTables);

                    scheduleOption = getScheduleOption(keyboard);

                    while (scheduleOption <= 0 || scheduleOption > tableNamesArrayList.size()) {
                        wrongOptionDisplaySchedules(displayOption, tableNamesArrayList);

                        scheduleOption = getScheduleOption(keyboard);
                    }

                } else {
                    System.out.println("\nNo schedules available.");
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }

        return scheduleOption;
    }

    /**
     * Displays the custom schedules that the user can choose from
     * @param displayOption A String that changes the output depending of the user's custom schedule manager choice
     * @param tableNamesArrayList An ArrayList holding the custom schedule names
     * @param customScheduleDBMetaTables A ResultSet containing the schedule names to be stored in tableNamesArrayList
     */
    private static void displaySchedules(String displayOption, ArrayList<String> tableNamesArrayList,
                                         ResultSet customScheduleDBMetaTables) {
        System.out.println("\nWhich schedule would you like to " + displayOption + "?");
        int i = 1;

        try {
            do {
                System.out.print("(" + i + ") ");
                tableNamesArrayList.add(customScheduleDBMetaTables.getString(3));
                System.out.println(tableNamesArrayList.get(i - 1));
                i++;
            } while (customScheduleDBMetaTables.next());
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
    }

    /**
     * Displays the schedules again if the user enters an incorrect option
     * @param displayOption A String to change the output depending on the user's custom schedule manager choice
     * @param tableNamesArrayList An ArrayList holding the custom schedule names
     */
    private static void wrongOptionDisplaySchedules(String displayOption, ArrayList<String> tableNamesArrayList) {
        System.out.println("\nWRONG OPTION!\n\n" +
                "Which schedule would you like to " + displayOption + "?");
        for (int i = 0; i < tableNamesArrayList.size(); i++) {
            System.out.print("(" + (i + 1) + ") ");
            System.out.println(tableNamesArrayList.get(i));
        }
    }

    /**
     * Gets the user's input for the schedule that is chosen
     * @param keyboard For user input
     * @return An int holding the user's schedule option
     */
    public static int getScheduleOption(Scanner keyboard) {
        int scheduleOption = 0;

        try {
            scheduleOption = keyboard.nextInt();
        } catch (InputMismatchException ex) {
            //ignore exception, prompt user again for input if input is incorrect
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
        keyboard.nextLine();//clear keyboard buffer

        return scheduleOption;
    }
}
