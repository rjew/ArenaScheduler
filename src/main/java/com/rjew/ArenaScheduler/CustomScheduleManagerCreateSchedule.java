package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Scanner;

final class CustomScheduleManagerCreateSchedule {

    private final static Logger logger = Logger.getLogger(CustomScheduleManagerCreateSchedule.class);

    /**
     * To prevent instantiation
     */
    private CustomScheduleManagerCreateSchedule() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Responsible for creating a new schedule
     * @param keyboard For getting user input
     * @return A String containing the new schedule name
     */
    public static String createSchedule(Scanner keyboard)  {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:Custom_Schedules;create=true"; //For db Connection

        try (Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);
             Statement customScheduleStatement =
                     customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            String scheduleName = getNewScheduleName(keyboard);

            String createTableSQLString = "CREATE TABLE \"" + scheduleName + "\" ( " +
                    "subject_id INTEGER NOT NULL, " +
                    "course_id CHAR(8) NOT NULL, " +
                    "course_title VARCHAR(40) NOT NULL, " +
                    "class_id INTEGER NOT NULL, " +
                    "seats INTEGER NOT NULL, " +
                    "code CHAR(1) NOT NULL, " +
                    "block INTEGER NOT NULL, " +
                    "room VARCHAR(15) NOT NULL, " +
                    "teacher VARCHAR(30))";
            customScheduleStatement.execute(createTableSQLString);
            System.out.println("\n" + scheduleName + " created.");

            return scheduleName;
        } catch (SQLException ex) {
            if (ex.getSQLState().equalsIgnoreCase("X0Y32")) {//X0Y32: If user enters a schedule name that already exists
                System.out.println("\nSchedule with the same name already exists.");
                return createSchedule(keyboard);
            } else {
                logger.error(ex);
                System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }

        return "";
    }

    /**
     * Gets user input for the new schedule name
     * @param keyboard For user input
     * @return A String with the new schedule name
     */
    public static String getNewScheduleName(Scanner keyboard) {
        String newScheduleName = "";

        try {
            System.out.println("\nEnter the new schedule name:");
            do {
                newScheduleName = keyboard.nextLine();
            } while(newScheduleName.trim().isEmpty());//Check if string is empty

        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }

        return newScheduleName;
    }
}
