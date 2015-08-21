package com.rjew.ArenaScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class CustomScheduleManagerDeleteClass {
    public static void deleteClass(Scanner keyboard, String tableName) {
        int classID;
        boolean deleteClassSuccessful;

        try {
            CustomScheduleManagerViewSchedule.viewSchedule(tableName);

            classID = getDeleteClassID(keyboard);

            deleteClassSuccessful = deleteCourse(classID, tableName);
            //todo if (deleteClassSuccessful), wrong classID?
            if (deleteClassSuccessful) {
                CustomScheduleManagerViewSchedule.viewSchedule(tableName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int getDeleteClassID(Scanner keyboard) {
        int classID = 0;

        try {
            System.out.println("Which class would you like to delete?\n" +
                    "Enter the Class ID of the course you would like to delete:");
            classID = keyboard.nextInt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return classID;
    }

    public static boolean deleteCourse(int classID, String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        try {

            Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

            Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            String deleteCourseSQLString = "DELETE  FROM \"" + tableName + "\" " +
                    "WHERE class_id = " + classID;

            customScheduleStatement.executeUpdate(deleteCourseSQLString);
            System.out.println("Class " + classID + " deleted.");

            customScheduleConn.close();
            customScheduleStatement.close();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
