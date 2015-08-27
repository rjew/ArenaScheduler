package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CustomScheduleManagerDeleteClass {

    final static Logger logger = Logger.getLogger(CustomScheduleManagerDeleteClass.class);

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

    public static int getDeleteClassID(Scanner keyboard) {
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
                System.out.println("WRONG OPTION!");
            }

            keyboard.nextLine();//clear keyboard buffer

        } while (classID < 1);

        return classID;
    }

    public static boolean deleteCourse(int classID, String tableName) {
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
