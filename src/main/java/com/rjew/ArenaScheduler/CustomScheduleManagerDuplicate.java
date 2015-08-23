package com.rjew.ArenaScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class CustomScheduleManagerDuplicate {
    public static void duplicateSchedule(Scanner keyboard, String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        try {

            Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

            Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            //todo check if same schedule name already exists
            String newScheduleName = CustomScheduleManagerCreateSchedule.getNewScheduleName(keyboard);

            String createDuplicateTableSQLString = "CREATE TABLE \"" + newScheduleName +
                    "\" AS SELECT * FROM \"" + tableName + "\" WITH NO DATA";

            String insertDuplicateTableSQLString = "INSERT INTO \"" + newScheduleName + "\" (subject_id, course_id, course_title, " +
                    "class_id, seats, code, block, room, teacher) " +
                    "SELECT subject_id, course_id, course_title, class_id, seats, code, block, room, teacher " +
                    "FROM \"" + tableName + "\"";

            customScheduleStatement.execute(createDuplicateTableSQLString);
            customScheduleStatement.executeUpdate(insertDuplicateTableSQLString);
            System.out.println(tableName + " has been copied to " + newScheduleName + ".");

            customScheduleConn.close();
            customScheduleStatement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
