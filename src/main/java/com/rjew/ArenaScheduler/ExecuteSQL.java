package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ExecuteSQL {

    final static Logger logger = Logger.getLogger(ExecuteSQL.class);

    public static void executeSQLStatement(String sqlStmt, Scanner keyboard) {
        final String ANNOUNCER_DB_URL = "jdbc:derby:Announcer_Fall_2015"; //For the db connection
        int numRows; //To hold the number of rows, the number of results
        int addClassOption; // To hold the option for adding a class to the custom schedule

        /* Connect to database and execute query, storing the results */
        try (Connection announcerConn = DriverManager.getConnection(ANNOUNCER_DB_URL);
             Statement announcerStatement =
                     announcerConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet announcerResultSet = announcerStatement.executeQuery(sqlStmt)
        ) {

            ResultSetMetaData announcerRSMeta = announcerResultSet.getMetaData();

            /* Get the number of results */
            numRows = getResultCount(announcerResultSet);

            System.out.println("\n" + numRows + " RESULTS.\n");

            if (numRows != 0) {
                CustomScheduleManagerViewSchedule.printSchedule(announcerResultSet, announcerRSMeta);

                do {
                    displayAddClassMenu();

                    addClassOption = getAddClassMenuOption(keyboard, announcerStatement);

                } while (addClassOption < 1 || addClassOption > 2);
            }

            System.out.println("\nConnection closed.");
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
    }

    public static int getResultCount(ResultSet announcerResultSet) {
        int numRows = 0;

        try {
            announcerResultSet.last();
            numRows = announcerResultSet.getRow();
            announcerResultSet.beforeFirst();
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }

        return numRows;
    }

    public static void displayAddClassMenu() {
        System.out.println("Would you like to add any of the classes to one of your schedules?\n" +
                "(1) Yes\n" +
                "(2) No");
    }

    public static int getAddClassMenuOption(Scanner keyboard, Statement announcerStatement) {
        int addClassOption = 0;

        try {
            addClassOption = keyboard.nextInt();
        } catch (InputMismatchException ex) {
            //ignore exception, prompt user again for input if input is incorrect
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
        keyboard.nextLine();//clear keyboard buffer

        switch (addClassOption) {
            case 1:
                CustomScheduleManagerSaveClass.saveClass(keyboard, announcerStatement);
                break;
            case 2:
                break;
            default:
                System.out.println("WRONG OPTION!");
        }

        return addClassOption;
    }
}
