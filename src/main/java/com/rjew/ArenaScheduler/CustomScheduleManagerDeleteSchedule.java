package com.rjew.ArenaScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CustomScheduleManagerDeleteSchedule {
    public static void deleteSchedule(String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        try (Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);
             Statement customScheduleStatement =
                     customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            String dropTableSQLString = "DROP TABLE \"" + tableName + "\"";

            customScheduleStatement.executeUpdate(dropTableSQLString);
            System.out.println(tableName + " deleted.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
