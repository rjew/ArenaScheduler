package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.List;

/**
 * concerned with collecting the necessary user input in order to
 * execute the specified daoManager method for the Custom Schedule database
 * and sends output to the user about the query
 */
class CustomScheduleManager {
    private static final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:Custom_Schedules;create=true"; //For db Connection

    private List<String> scheduleNamesList;
    private final DAOManager daoManager;

    /**
     * Initializes daoManager and scheduleNamesList fields
     * @throws SQLException
     */
    public CustomScheduleManager() throws SQLException {
        daoManager = new DAOManager(CUSTOM_SCHEDULE_DB_URL);
        scheduleNamesList = daoManager.getScheduleNames();
    }

    /**
     * Displays the available custom schedules and returns the user selected schedule name
     * @param displayOption For the output corresponding to the custom schedule modification option
     * @return A String holding the user's selected schedule name
     */
    public String getScheduleName(String displayOption) {
        int scheduleOption;

        if (scheduleNamesList.size() != 0) {

            displaySchedules(displayOption);

            scheduleOption = ScannerUtils.getInt();

            while (scheduleOption <= 0 || scheduleOption > scheduleNamesList.size()) {
                System.out.println("\nWRONG OPTION!");
                displaySchedules(displayOption);

                scheduleOption = ScannerUtils.getInt();
            }

            return (scheduleNamesList.get(scheduleOption - 1));

        } else {
            System.out.println("\nNo schedules available.");

            return "";
        }
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
     * @param course A Course object that holds the course that the user wants to add
     * @throws SQLException
     */
    public void saveCourse(Course course) throws SQLException {
        int scheduleOption;
        boolean addClassSuccessful;

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
                    System.out.println("\nClass " + course.getClassID() + " has been added to " + scheduleNamesList.get(scheduleOption - 1) + ".");
                }
                viewSchedule(scheduleNamesList.get(scheduleOption - 1));
            } else {
                System.out.println("\nCreating new schedule");
                addCourse(course);
            }
        } else {
            System.out.println("\nNo schedule found... Creating new schedule");
            addCourse(course);
        }
    }

    /**
     * Creates a new schedule and adds the specified course to the schedule
     * @param course A Course object that holds the course that the user wants to add
     * @throws SQLException
     */
    private void addCourse(Course course) throws SQLException {
        boolean addCourseSuccessful;
        String scheduleName;

        scheduleName = createSchedule();

        addCourseSuccessful = daoManager.addCourse(course, scheduleName);
        if (addCourseSuccessful) {
            System.out.println("Class " + course.getClassID() + " has been added to " + scheduleName + ".");
        }
        viewSchedule(scheduleName);
    }

    /**
     * Gets the class ID of the course that the user wants to delete and removes it from the specified schedule
     * @param scheduleName The schedule name in which the course will be removed
     * @throws SQLException
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

    /**
     * Gets the new schedule name and creates the new schedule
     * @return A String holding the new schedule name
     * @throws SQLException
     */
    public String createSchedule() throws SQLException {
        String scheduleName = "";
        boolean createScheduleSuccessful = false;

        do {
            try {
                System.out.println("\nEnter the new schedule name:");
                scheduleName = ScannerUtils.getString();

                createScheduleSuccessful = daoManager.createSchedule(scheduleName);

                System.out.println("\n" + scheduleName + " created.");
                scheduleNamesList = daoManager.getScheduleNames();//Refresh schedule names list
            } catch (SQLException ex) {
                if (ex.getSQLState().equalsIgnoreCase("X0Y32")) {//X0Y32: If the user enters a schedule name that already exists
                    System.out.println("\nSchedule with the same name already exists.");
                } else {
                    throw ex;
                }
            }
        } while (!createScheduleSuccessful);

        return scheduleName;
    }

    /**
     * Deletes the specified schedule
     * @param scheduleName The name of the schedule to be deleted
     * @throws SQLException
     */
    public void deleteSchedule(String scheduleName) throws SQLException {
        daoManager.deleteSchedule(scheduleName);
        System.out.println("\n" + scheduleName + " deleted.");
        scheduleNamesList = daoManager.getScheduleNames();//Refresh schedule names list
    }

    /**
     * Responsible for renaming the specified schedule
     * @param scheduleName Holds the schedule to be renamed
     * @throws SQLException
     */
    public void renameSchedule(String scheduleName) throws SQLException {
        boolean renameScheduleSuccessful = false;
        String newScheduleName;

        do {
            try {
                System.out.println("\nEnter the new schedule name:");
                newScheduleName = ScannerUtils.getString();

                renameScheduleSuccessful = daoManager.renameSchedule(scheduleName, newScheduleName);

                System.out.println("\n" + scheduleName + " is now renamed to " + newScheduleName + ".");
                scheduleNamesList = daoManager.getScheduleNames();//Refresh the list of schedule names
            } catch (SQLException ex) {
                if (ex.getSQLState().equalsIgnoreCase("X0Y32")) {//X0Y32: If the user enters a schedule name that already exists
                    System.out.println("\nSchedule with the same name already exists.");
                } else {
                    throw ex;
                }
            }
        } while (!renameScheduleSuccessful);
    }

    /**
     * Responsible for making a copy of a schedule
     * @param scheduleName The schedule name to be duplicated
     * @throws SQLException
     */
    public void duplicateSchedule(String scheduleName) throws SQLException {
        boolean duplicateScheduleSuccessful = false;
        String duplicateScheduleName;

        do {
            try {

                System.out.println("\nEnter the duplicate schedule name:");
                duplicateScheduleName = ScannerUtils.getString();

                duplicateScheduleSuccessful = daoManager.duplicateSchedule(scheduleName, duplicateScheduleName);

                System.out.println("\n" + scheduleName + " has been copied to " + duplicateScheduleName + ".");
                scheduleNamesList = daoManager.getScheduleNames();//Refresh the list of schedule names
            } catch (SQLException ex) {
                if (ex.getSQLState().equalsIgnoreCase("X0Y32")) {//X0Y32: If the user enters a schedule name that already exists
                    System.out.println("\nSchedule with the same name already exists.");
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
     * @throws SQLException
     */
    public boolean viewSchedule(String scheduleName) throws SQLException {
        String sqlStatement = "SELECT subject_id, course_id, " +
                "course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM \"" + scheduleName + "\"" +
                "ORDER BY block";

        List<Course> courseList = daoManager.executeSelectQuery(sqlStatement);

        int numRows = DAOUtils.getCourseCount(courseList);

        if (numRows != 0) {
            DAOUtils.printSchedule(courseList);
            return true;
        } else {
            System.out.println("\n" + scheduleName + " has no classes!");
            return false;
        }
    }
}
