package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Scanner;

final class CustomScheduleManagerRename {

    private final static Logger logger = Logger.getLogger(CustomScheduleManagerRename.class);

    /**
     * To prevent instantiation
     */
    private CustomScheduleManagerRename() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Responsible for renaming a schedule
     * @param keyboard For user input
     * @param tableName The name of the schedule to be renamed
     */
    public static void renameSchedule(Scanner keyboard, String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:Custom_Schedules;create=true"; //For db Connection

        try (Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);
             Statement customScheduleStatement =
                     customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            String newScheduleName = CustomScheduleManagerCreateSchedule.getNewScheduleName(keyboard);

            String renameTableSQLString = "RENAME TABLE \"" + tableName +
                    "\" TO \"" + newScheduleName + "\"";

            customScheduleStatement.executeUpdate(renameTableSQLString);
            System.out.println("\n" + tableName + " is now renamed to " + newScheduleName + ".");
        } catch (SQLException ex) {
            if (ex.getSQLState().equalsIgnoreCase("X0Y32")) {//X0Y32: If the user enters a schedule name that already exists
                System.out.println("\nSchedule with the same name already exists.");
                renameSchedule(keyboard, tableName);
            } else {
                logger.error(ex);
                System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
            }
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
    }
}
