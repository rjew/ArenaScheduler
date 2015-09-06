package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.List;

public class CustomScheduleManager {
    private static final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:Custom_Schedules;create=true"; //For db Connection

    private List<String> scheduleNamesList;
    private DAOManager daoManager;

    public CustomScheduleManager() throws SQLException {
        daoManager = new DAOManager(CUSTOM_SCHEDULE_DB_URL);
        scheduleNamesList = daoManager.getScheduleNames();
    }

    /**
     * Responsible for displaying the custom schedules that the user can select from
     * @param displayOption A String to modify the output depending on the custom schedule manager option the user chooses
     * @return An int containing the schedule option the user chooses, for the index of the tableNamesArrayList
     */
    public int displayScheduleOptions(String displayOption) throws SQLException {
        int scheduleOption = 0;

        if (scheduleNamesList.size() != 0) {

            displaySchedules(displayOption);

            scheduleOption = ScannerUtils.getInt();

            while (scheduleOption <= 0 || scheduleOption > scheduleNamesList.size()) {
                System.out.println("\nWRONG OPTION!\n");
                displaySchedules(displayOption);

                scheduleOption = ScannerUtils.getInt();
            }

        } else {
            System.out.println("\nNo schedules available.");
        }

        return scheduleOption;
    }

    /**
     * Displays the custom schedules that the user can choose from
     * @param displayOption A String that changes the output depending of the user's custom schedule manager choice
     */
    private void displaySchedules(String displayOption) {
        System.out.println("\nWhich schedule would you like to " + displayOption + "?");

        for (int i = 0; i < scheduleNamesList.size(); i++) {
            System.out.print("(" + (i + 1) + ") ");
            System.out.println(scheduleNamesList.get(i));
        }
    }

    /**
     * Responsible for saving a class that the user selects from the search catalog
     */
    public void saveCourse(Course course) throws SQLException {
        int classID;
        int scheduleOption;
        boolean addClassSuccessful;

        do {
            System.out.println("\nWhich class would you like to add to your schedule?\n" +
                    "Enter the Class ID of the course you would like to add:");
            classID = ScannerUtils.getInt();
            if (classID < 1) {
                System.out.println("\nWRONG OPTION!");
            }
        } while (classID < 1);

        if (scheduleNamesList.size() != 0) {

            do {
                displaySchedules("add the class to");
                System.out.println("(" + (scheduleNamesList.size() + 1) + ") Create new schedule");

                scheduleOption = ScannerUtils.getInt();

                if (scheduleOption < 1 || scheduleOption > scheduleNamesList.size() + 1) {
                    System.out.println("WRONG OPTION!");
                }
            } while (scheduleOption < 1 || scheduleOption > scheduleNamesList.size() + 1);

            if (scheduleOption != scheduleNamesList.size() + 1) {
                addClassSuccessful = daoManager.addCourse(course, scheduleNamesList.get(scheduleOption - 1));
                if (addClassSuccessful) {
                    System.out.println("\nClass " + classID + " has been added to " + scheduleNamesList.get(scheduleOption - 1) + ".");
                }
                viewSchedule(scheduleNamesList.get(scheduleOption - 1));
            } else {
                System.out.println("\nCreating new schedule");
                addCourse(course, classID);
            }
        } else {
            System.out.println("\nNo schedule found... Creating new schedule");
            addCourse(course, classID);
        }
    }

    private void addCourse(Course course, int classID) throws SQLException {
        boolean addCourseSuccessful;
        boolean createScheduleSuccessful = false;
        String scheduleName = "";

        do {
            try {
                System.out.println("\nEnter the new schedule name:");
                scheduleName = ScannerUtils.getString();
                createScheduleSuccessful = daoManager.createSchedule(scheduleName);
            } catch (SQLException ex) {
                if (ex.getSQLState().equalsIgnoreCase("X0Y32")) {//X0Y32: If user enters a schedule name that already exists
                    System.out.println("\nSchedule with the same name already exists.");
                } else {
                    throw ex;//todo test if this exception works
                }
            }
        } while (!createScheduleSuccessful);

        addCourseSuccessful = daoManager.addCourse(course, scheduleName);
        if (addCourseSuccessful) {
            System.out.println("Class " + classID + " has been added to " + scheduleName + ".");
        }
        viewSchedule(scheduleName);
    }

    /**
     * Responsible for deleting a class from a schedule
     * @param scheduleName The schedule to delete the class from
     */
    public void deleteCourse(String scheduleName) throws SQLException {
        int classID;
        boolean deleteClassSuccessful;
        boolean properSchedule;

        properSchedule = viewSchedule(scheduleName);

        if (properSchedule) {
            do {
                System.out.println("Which class would you like to delete?\n" +
                        "Enter the Class ID of the course you would like to delete:");
                classID = ScannerUtils.getInt();

                if (classID < 1) {
                    System.out.println("\nWRONG OPTION!\n");
                }
            } while (classID < 1);

            deleteClassSuccessful = daoManager.deleteCourse(classID, scheduleName);

            if (deleteClassSuccessful) {
                System.out.println("\nClass " + classID + " deleted.");
                viewSchedule(scheduleName);
            } else {
                System.out.println("\nClass " + classID + " does not exist in " + scheduleName + ".");
                deleteCourse(scheduleName);
            }
        }
    }

    public void createSchedule(String scheduleName) throws SQLException {
        daoManager.createSchedule(scheduleName);
        scheduleNamesList = daoManager.getScheduleNames();//Refresh schedule names list
    }

    public void deleteSchedule(String scheduleName) throws SQLException {
        daoManager.deleteSchedule(scheduleName);
        scheduleNamesList = daoManager.getScheduleNames();//Refresh schedule names list
    }

    /**
     * Responsible for renaming a schedule
     */
    public void renameSchedule(String scheduleName) throws SQLException {
        boolean renameScheduleSuccessful = false;

        do {
            try {
                System.out.println("\nEnter the new schedule name:");
                String newScheduleName = ScannerUtils.getString();

                renameScheduleSuccessful = daoManager.renameSchedule(scheduleName, newScheduleName);
                scheduleNamesList = daoManager.getScheduleNames();//Refresh the list of schedule names
            } catch (SQLException ex) {
                if (ex.getSQLState().equalsIgnoreCase("X0Y32")) {//X0Y32: If the user enters a schedule name that already exists
                    System.out.println("\nSchedule with the same name already exists.");
                } else {
                    throw ex;
                }
            }
        } while (!renameScheduleSuccessful);//todo check this exception
    }

    /**
     * Responsible for making a copy of a schedule
     * @param scheduleName The schedule name to be copied
     */
    public void duplicateSchedule(String scheduleName) throws SQLException {
        boolean duplicateScheduleSuccessful = false;

        do {
            try {

                System.out.println("\nEnter the duplicate schedule name:");
                String duplicateScheduleName = ScannerUtils.getString();

                duplicateScheduleSuccessful = daoManager.duplicateSchedule(scheduleName, duplicateScheduleName);
                scheduleNamesList = daoManager.getScheduleNames();//Refresh the list of schedule names
            } catch (SQLException ex) {
                if (ex.getSQLState().equalsIgnoreCase("X0Y32")) {//X0Y32: If the user enters a schedule name that already exists
                    System.out.println("\nSchedule with the same name already exists.");
                    duplicateSchedule(scheduleName);
                } else {
                    throw ex;
                }
            }
        } while (!duplicateScheduleSuccessful);

    }

    /**
     * Responsible for showing the schedule the user selected
     * @param scheduleName The schedule name the user want to view
     * @return A boolean indicating whether or not the schedule was successfully printed,
     * true=schedule printed successfully, false=schedule did not print successfully
     */
    public boolean viewSchedule(String scheduleName) throws SQLException {
        String sqlStatement = "SELECT subject_id, course_id, " +
                "course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM \"" + scheduleName + "\"" +
                "ORDER BY block";

        List<Course> courseList = daoManager.executeSelectQuery(sqlStatement);

        int numRows = DAOUtils.getResultCount(courseList);

        if (numRows != 0) {
            DAOUtils.printSchedule(courseList);
            return true;
        } else {
            System.out.println("\n" + scheduleName + " has no classes!");
            return false;
        }
    }
}
