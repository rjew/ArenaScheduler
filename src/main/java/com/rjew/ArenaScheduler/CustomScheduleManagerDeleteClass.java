package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

class CustomScheduleManagerDeleteClass {

    private final static Logger logger = Logger.getLogger(CustomScheduleManagerDeleteClass.class);

    /**
     * Responsible for deleting a class from a schedule
     * @param keyboard For user input
     * @param tableName The schedule to delete the class from
     */
    public static void deleteClass(Scanner keyboard, String tableName) {
        int classID;
        boolean deleteClassSuccessful;
        boolean properSchedule;

        try {
            properSchedule = CustomScheduleManagerViewSchedule.viewSchedule(tableName);

            if (properSchedule) {
                classID = getDeleteClassID(keyboard);

                deleteClassSuccessful = deleteCourse(classID, tableName);

                if (deleteClassSuccessful) {
                    CustomScheduleManagerViewSchedule.viewSchedule(tableName);
                } else {
                    deleteClass(keyboard, tableName);
                }
            }

        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
    }

    /**
     * Get the class ID of the class to be deleted
     * @param keyboard For user input
     * @return An int for the class ID
     */
    private static int getDeleteClassID(Scanner keyboard) {
        int classID = 0;

        do {
            System.out.println("Which class would you like to delete?\n" +
                    "Enter the Class ID of the course you would like to delete:");
            try {
                classID = keyboard.nextInt();
            } catch (InputMismatchException ex) {
                //ignore exception, prompt user again for input if input is incorrect
            } catch (Exception ex) {
                logger.error(ex);
                System.err.println("ERROR: " + ex.getMessage());
            }

            if (classID < 1) {
                System.out.println("\nWRONG OPTION!\n");
            }

            keyboard.nextLine();//clear keyboard buffer

        } while (classID < 1);

        return classID;
    }

    /**
     * Responsible for executing the sql query to delete the course from the specified schedule
     * @param classID The classID for the class to be removed
     * @param tableName The schedule in which the class will be removed
     * @return A boolean indicating whether or not the class was removed successfully,
     * true=class successfully deleted, false=class could not be deleted
     */
    private static boolean deleteCourse(int classID, String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:Custom_Schedules;create=true"; //For db Connection

        int rowsChanged;

        try (Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);
             Statement customScheduleStatement =
                     customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            String deleteCourseSQLString = "DELETE  FROM \"" + tableName + "\" " +
                    "WHERE class_id = " + classID;

            rowsChanged = customScheduleStatement.executeUpdate(deleteCourseSQLString);

            if (rowsChanged != 0) {
                System.out.println("\nClass " + classID + " deleted.");

                return true;
            } else {
                System.out.println("\nClass " + classID + " does not exist in " + tableName + ".");

                return false;
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }

        return false;
    }
}
