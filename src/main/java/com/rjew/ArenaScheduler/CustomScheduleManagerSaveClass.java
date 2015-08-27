package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CustomScheduleManagerSaveClass {

    final static Logger logger = Logger.getLogger(CustomScheduleManagerSaveClass.class);

    public static void saveClass(Scanner keyboard, Statement announcerStatement) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:Custom_Schedules;create=true"; //For db Connection

        int classID;
        int scheduleOption;
        int createNewScheduleOption;
        boolean addClassSuccessful;
        ArrayList<String> tableNamesArrayList = new ArrayList<>();

        try (Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);
             Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE)
        ) {

            classID = getAddClassID(keyboard);

            DatabaseMetaData customScheduleDBMeta = customScheduleConn.getMetaData();

            try (ResultSet customScheduleDBMetaTables =
                         customScheduleDBMeta.getTables(null, null, "%", new String[] {"TABLE"})) {

                if (customScheduleDBMetaTables.next()) {
                    System.out.println("\nWhich schedule would you like to add the class to?");

                    createNewScheduleOption = displaySchedulesOrNewSchedule(tableNamesArrayList, customScheduleDBMetaTables);

                    scheduleOption = CustomScheduleManagerSelection.getScheduleOption(keyboard);

                    while (scheduleOption < 1 || scheduleOption > tableNamesArrayList.size() + 1) {
                        wrongOptionDisplaySchedulesOrNewSchedule(tableNamesArrayList);

                        scheduleOption = CustomScheduleManagerSelection.getScheduleOption(keyboard);
                    }

                    if (scheduleOption != createNewScheduleOption) {
                        addClassSuccessful = CustomScheduleManagerAddCourse.addCourse(classID, tableNamesArrayList.get(scheduleOption - 1),
                                announcerStatement, customScheduleStatement);
                        if (addClassSuccessful) {
                            System.out.println("\nClass " + classID + " has been added to " + tableNamesArrayList.get(scheduleOption - 1) + ".");
                        }
                        CustomScheduleManagerViewSchedule.viewSchedule(tableNamesArrayList.get(scheduleOption - 1));
                    } else {
                        System.out.println("\nCreating new schedule");
                        String scheduleName = CustomScheduleManagerCreateSchedule.createSchedule(keyboard);
                        addClassSuccessful = CustomScheduleManagerAddCourse.addCourse(classID, scheduleName,
                                announcerStatement, customScheduleStatement);
                        if (addClassSuccessful) {
                            System.out.println("Class " + classID + " has been added to " + scheduleName + ".");
                        }
                        CustomScheduleManagerViewSchedule.viewSchedule(scheduleName);
                    }

                } else {
                    System.out.println("\nNo schedule found... Creating new schedule");
                    String scheduleName = CustomScheduleManagerCreateSchedule.createSchedule(keyboard);
                    addClassSuccessful = CustomScheduleManagerAddCourse.addCourse(classID, scheduleName,
                            announcerStatement, customScheduleStatement);
                    if (addClassSuccessful) {
                        System.out.println("Class " + classID + " has been added to " + scheduleName + ".");
                    }
                    CustomScheduleManagerViewSchedule.viewSchedule(scheduleName);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
    }

    public static int getAddClassID(Scanner keyboard) {
        int classID = 0;

        do {
            System.out.println("\nWhich class would you like to add to your schedule?\n" +
                    "Enter the Class ID of the course you would like to add:");
            try {
                classID = keyboard.nextInt();
            } catch (InputMismatchException ex) {
                //ignore exception, prompt user again for input if input is incorrect
            } catch (Exception ex) {
                logger.error(ex);
                System.err.println("ERROR: " + ex.getMessage());
            }
            keyboard.nextLine();//clear keyboard buffer

            if (classID < 1) {
                System.out.println("\nWRONG OPTION!");
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
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }

        return i;
    }

    public static void wrongOptionDisplaySchedulesOrNewSchedule(ArrayList<String> tableNamesArrayList) {
        System.out.println("\nWRONG OPTION\n\n" +
                "Which schedule would you like to add the class to?");
        for (int i = 0; i < tableNamesArrayList.size(); i++) {
            System.out.print("(" + (i + 1) + ") ");
            System.out.println(tableNamesArrayList.get(i));
        }
        System.out.println("(" + (tableNamesArrayList.size() + 1) + ") " +
                "Create new schedule");
    }
}
