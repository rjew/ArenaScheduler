package com.rjew.ArenaScheduler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
//todo fix errors involving the scenario where no schedules exist
public class CustomScheduleManagerSelection {
    public static int displayScheduleOptions(Scanner keyboard, ArrayList<String> tableNamesArrayList,
                                             String displayOption) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        int scheduleOption = 0;

        tableNamesArrayList.clear();

        try (Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL)) {

            DatabaseMetaData customScheduleDBMeta = customScheduleConn.getMetaData();

            try (ResultSet customScheduleDBMetaTables =
                         customScheduleDBMeta.getTables(null, null, "%", new String[]{"TABLE"})) {

                if (customScheduleDBMetaTables.next()) {

                    displaySchedules(displayOption, tableNamesArrayList, customScheduleDBMetaTables);

                    scheduleOption = getScheduleOption(keyboard);

                    while (scheduleOption <= 0 || scheduleOption > tableNamesArrayList.size()) {
                        wrongOptionDisplaySchedules(displayOption, tableNamesArrayList);

                        scheduleOption = getScheduleOption(keyboard);
                    }

                } else {
                    System.out.println("No schedules available.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return scheduleOption;
    }

    public static void displaySchedules(String displayOption, ArrayList<String> tableNamesArrayList,
                                        ResultSet customScheduleDBMetaTables) {
        System.out.println("Which schedule would you like to " + displayOption + "?");
        int i = 1;

        try {
            do {
                System.out.print("(" + i + ") ");
                tableNamesArrayList.add(customScheduleDBMetaTables.getString(3));
                System.out.println(tableNamesArrayList.get(i - 1));
                i++;
            } while (customScheduleDBMetaTables.next());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void wrongOptionDisplaySchedules(String displayOption, ArrayList<String> tableNamesArrayList) {
        System.out.println("WRONG OPTION\n" +
                "Which schedule would you like to " + displayOption + "?");
        for (int i = 0; i < tableNamesArrayList.size(); i++) {
            System.out.print("(" + (i + 1) + ") ");
            System.out.println(tableNamesArrayList.get(i));
        }
    }

    public static int getScheduleOption(Scanner keyboard) {
        int scheduleOption = 0;

        try {
            scheduleOption = keyboard.nextInt();
        } catch (InputMismatchException ex) {
            //ignore exception, prompt user again for input if input is incorrect
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        keyboard.nextLine();//clear keyboard buffer

        return scheduleOption;
    }
}
