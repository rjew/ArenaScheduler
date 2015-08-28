package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.*;

class CustomScheduleManagerDeleteSchedule {

    private final static Logger logger = Logger.getLogger(CustomScheduleManagerDeleteSchedule.class);

    /**
     * Responsible for deleted a schedule
     * @param tableName The name of the schedule to be deleted
     */
    public static void deleteSchedule(String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:Custom_Schedules;create=true"; //For db Connection

        try (Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);
             Statement customScheduleStatement =
                     customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            String dropTableSQLString = "DROP TABLE \"" + tableName + "\"";

            customScheduleStatement.executeUpdate(dropTableSQLString);
            System.out.println("\n" + tableName + " deleted.");

        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
    }
}
