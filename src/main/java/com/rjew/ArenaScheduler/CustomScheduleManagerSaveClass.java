package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CustomScheduleManagerSaveClass {
    public static void saveClass(Scanner keyboard, Statement announcerStatement) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        int classID;
        int scheduleOption;
        int createNewScheduleOption;
        boolean addClassSucessful;
        ArrayList<String> tableNamesArrayList = new ArrayList<String>();

        try {
            Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

            Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            classID = getAddClassID(keyboard);

            DatabaseMetaData customScheduleDBMeta = customScheduleConn.getMetaData();
            ResultSet customScheduleDBMetaTables = customScheduleDBMeta.getTables(null, null, "%", new String[] {"TABLE"});
            if (customScheduleDBMetaTables.next()) {
                System.out.println("Which schedule would you like to add the class to?");

                createNewScheduleOption = displaySchedulesOrNewSchedule(tableNamesArrayList, customScheduleDBMetaTables);

                scheduleOption = CustomScheduleManagerSelection.getScheduleOption(keyboard);

                while (scheduleOption < 1 || scheduleOption > tableNamesArrayList.size() + 1) {
                    wrongOptionDisplaySchedulesOrNewSchedule(tableNamesArrayList);

                    scheduleOption = CustomScheduleManagerSelection.getScheduleOption(keyboard);
                }

                if (scheduleOption != createNewScheduleOption) {
                    addClassSucessful = CustomScheduleManagerAddCourse.addCourse(classID, tableNamesArrayList.get(scheduleOption - 1),
                            announcerStatement, customScheduleStatement);
                    //todo if (addClassSuccessful)
                    CustomScheduleManagerViewSchedule.viewSchedule(tableNamesArrayList.get(scheduleOption - 1));
                } else {
                    System.out.println("Creating new schedule");
                    String scheduleName = CustomScheduleManagerCreateSchedule.createSchedule(keyboard);
                    addClassSucessful = CustomScheduleManagerAddCourse.addCourse(classID, scheduleName,
                            announcerStatement, customScheduleStatement);
                    //todo if (addClassSuccessful)
                    CustomScheduleManagerViewSchedule.viewSchedule(scheduleName);
                }

            } else {
                System.out.println("No schedule found... Creating new schedule");
                String scheduleName = CustomScheduleManagerCreateSchedule.createSchedule(keyboard);
                addClassSucessful = CustomScheduleManagerAddCourse.addCourse(classID, scheduleName,
                        announcerStatement, customScheduleStatement);
                //todo if (addClassSuccessful)
                CustomScheduleManagerViewSchedule.viewSchedule(scheduleName);
            }

            customScheduleConn.close();
            customScheduleDBMetaTables.close();
            customScheduleStatement.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int getAddClassID(Scanner keyboard) {
        int classID = 0;

        do {
            System.out.println("Which class would you like to add to your schedule?\n" +
                    "Enter the Class ID of the course you would like to add:");
            try {
                classID = keyboard.nextInt();
            } catch (InputMismatchException ex) {
                //ignore exception, prompt user again for input if input is incorrect
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            keyboard.nextLine();//clear keyboard buffer

            if (classID < 1) {
                System.out.println("WRONG OPTION!");
            }
        } while (classID < 1);

        return classID;
    }

    public static int displaySchedulesOrNewSchedule(ArrayList<String> tableNamesArrayList,
                                                    ResultSet customScheduleDBMetaTables) {
        int i = 1;

        try {
            do {
                System.out.print("(" + i + ") ");
                tableNamesArrayList.add(customScheduleDBMetaTables.getString(3));
                System.out.println(tableNamesArrayList.get(i - 1));
                i++;
            } while (customScheduleDBMetaTables.next());
            System.out.println("(" + (i) + ") " +
                    "Create new schedule");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return i;
    }

    public static void wrongOptionDisplaySchedulesOrNewSchedule(ArrayList<String> tableNamesArrayList) {
        System.out.println("WRONG OPTION\n" +
                "Which schedule would you like to add the class to?");
        for (int i = 0; i < tableNamesArrayList.size(); i++) {
            System.out.print("(" + (i + 1) + ") ");
            System.out.println(tableNamesArrayList.get(i));
        }
        System.out.println("(" + (tableNamesArrayList.size() + 1) + ") " +
                "Create new schedule");
    }
}
