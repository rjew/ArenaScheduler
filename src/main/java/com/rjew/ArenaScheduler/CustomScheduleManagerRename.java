package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.Scanner;

public class CustomScheduleManagerRename {
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
            System.out.println(tableName + " is now renamed to " + newScheduleName + ".");
        } catch (SQLException ex) {
            if (ex.getSQLState().equalsIgnoreCase("X0Y32")) {
                System.out.println("Schedule with the same name already exists.");
                renameSchedule(keyboard, tableName);
            } else {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
