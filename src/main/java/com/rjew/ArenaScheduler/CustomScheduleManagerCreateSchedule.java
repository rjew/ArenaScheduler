package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.Scanner;

public class CustomScheduleManagerCreateSchedule {
    public static String createSchedule(Scanner keyboard)  {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

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
            System.out.println(scheduleName + " created.");

            return scheduleName;
        } catch (SQLException ex) {
            if (ex.getSQLState().equalsIgnoreCase("X0Y32")) {
                System.out.println("Schedule with the same name already exists.");
                createSchedule(keyboard);
            } else {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public static String getNewScheduleName(Scanner keyboard) {
        String newScheduleName = "";

        try {
            System.out.println("Enter the new schedule name:");
            do {
                newScheduleName = keyboard.nextLine();
            } while(newScheduleName.trim().isEmpty());//Check if string is empty

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return newScheduleName;
    }
}
