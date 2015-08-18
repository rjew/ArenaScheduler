package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.Scanner;

public class ExecuteSQL {
    public static void executeSQLStatement(String sqlStmt, Scanner keyboard) {
        final String ANNOUNCER_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Announcer_Fall_2015"; //For the db connection
        int numRows; //To hold the number of rows, the number of results
        int addClassOption; // To hold the option for adding a class to the custom schedule

        try {

            /* Connect to database and execute query, storing the results */
            Connection announcerConn = DriverManager.getConnection(ANNOUNCER_DB_URL);

            PreparedStatement announcerStatement = announcerConn.prepareStatement(sqlStmt, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet announcerResultSet = announcerStatement.executeQuery();

            ResultSetMetaData announcerRSMeta = announcerResultSet.getMetaData();

            /* Get the number of results */
            numRows = getResultCount(announcerResultSet);

            System.out.println("\n" + numRows + " RESULTS.\n");

            if (numRows != 0) {
                CustomScheduleManager.printSchedule(announcerResultSet, announcerRSMeta);

                do {
                    displayAddClassMenu();

                    addClassOption = getAddClassMenuOption(keyboard);

                    switch (addClassOption) {
                        case 1:
                            saveClass(keyboard, announcerStatement);
                            break;
                        case 2:
                            break;
                        default:
                            System.out.println("WRONG OPTION!");
                    }
                } while (addClassOption < 1 || addClassOption > 2);
            }

            announcerStatement.close();
            announcerConn.close();
            announcerResultSet.close();
            System.out.println("\nConnection closed.");
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    public static int getResultCount(ResultSet announcerResultSet) {
        int numRows = 0;

        try {
            announcerResultSet.last();
            numRows = announcerResultSet.getRow();
            announcerResultSet.beforeFirst();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return numRows;
    }

    public static void displayAddClassMenu() {
        System.out.println("Would you like to add any of the classes to one of your schedules?\n" +
                "(1) Yes\n" +
                "(2) No");
    }

    public static int getAddClassMenuOption(Scanner keyboard) {
        int addClassOption = 0;

        try {
            addClassOption = keyboard.nextInt();
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        return addClassOption;
    }
}
