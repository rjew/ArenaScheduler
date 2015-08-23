package com.rjew.ArenaScheduler;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CustomScheduleManagerRename {
    public static void renameSchedule(Scanner keyboard, String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        try {

            Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

            Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            String newScheduleName = CustomScheduleManagerCreateSchedule.getNewScheduleName(keyboard);

            String renameTableSQLString = "RENAME TABLE \"" + tableName +
                    "\" TO \"" + newScheduleName + "\"";

            customScheduleStatement.executeUpdate(renameTableSQLString);
            System.out.println(tableName + " is now renamed to " + newScheduleName + ".");

            customScheduleConn.close();
            customScheduleStatement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
