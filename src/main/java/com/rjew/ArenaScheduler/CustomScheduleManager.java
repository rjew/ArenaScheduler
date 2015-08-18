package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class CustomScheduleManager {
    public static void accessCustomSchedules(Scanner keyboard) {
        int menuOption;
        int scheduleOption;
        ArrayList<String> tableNamesArrayList = new ArrayList<String>();

        do {
            displayCustomSchedulesMenu();

            menuOption = getCustomSchedulesMenuOption(keyboard);

            switch (menuOption) {
                case 1://todo fix this case so the user doesn't have to select a schedule again
                    final String ANNOUNCER_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Announcer_Fall_2015"; //For the db connection
                    try {
                        Connection announcerConn = DriverManager.getConnection(ANNOUNCER_DB_URL);

                        Statement announcerStatement = announcerConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                ResultSet.CONCUR_READ_ONLY);

                        saveClass(keyboard, announcerStatement);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case 2:
                    scheduleOption = displayScheduleOptions(keyboard, tableNamesArrayList, "select");
                    if (scheduleOption != 0) {
                        deleteClass(keyboard, tableNamesArrayList.get(scheduleOption - 1));
                    }
                    break;
                case 3:
                    final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection
                    try {
                        Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

                        Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);

                        createTable(keyboard, customScheduleStatement);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case 4:
                    scheduleOption = displayScheduleOptions(keyboard, tableNamesArrayList, "delete");
                    if (scheduleOption != 0) {
                        deleteSchedule(tableNamesArrayList.get(scheduleOption - 1));
                    }
                    break;
                case 5:
                    scheduleOption = displayScheduleOptions(keyboard, tableNamesArrayList, "view");
                    if (scheduleOption != 0) {
                        viewSchedule(tableNamesArrayList.get(scheduleOption - 1));
                    }
                    break;
                case 6:
                    break;
                default:
                    System.out.println("WRONG OPTION!");
            }

        } while (menuOption != 5);
    }

    public static void displayCustomSchedulesMenu() {
        System.out.println("What would you like to do?\n" +
                "(1) Add a course to a schedule\n" +
                "(2) Delete a course from a schedule\n" +
                "(3) Create a new schedule\n" +
                "(4) Delete a schedule\n" +
                "(5) View one of your schedules\n" +
                "(6) Quit");
    }

    public static int getCustomSchedulesMenuOption(Scanner keyboard) {
        int menuOption = 0;

        try {
            menuOption = keyboard.nextInt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return menuOption;
    }

    //todo weird bug in displayScheduleOptions where the check for the scheduleOption doesn't work after many uses, clear tablenamesarraylsit
    public static int displayScheduleOptions(Scanner keyboard, ArrayList<String> tableNamesArrayList,
                                             String displayOption) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        int scheduleOption = 0;

        tableNamesArrayList.clear();

        try {
            Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

            DatabaseMetaData customScheduleDBMeta = customScheduleConn.getMetaData();
            ResultSet customScheduleDBMetaTables = customScheduleDBMeta.getTables(null, null, "%", new String[]{"TABLE"});

            if (customScheduleDBMetaTables.next()) {
                System.out.println("Which schedule would you like to " + displayOption + "?");
                int i = 1;
                do {
                    System.out.print("(" + i + ") ");
                    tableNamesArrayList.add(customScheduleDBMetaTables.getString(3));
                    System.out.println(tableNamesArrayList.get(i - 1));
                    i++;
                } while (customScheduleDBMetaTables.next());

                try {//todo check if user types in wrong input
                    scheduleOption = keyboard.nextInt();
                } catch (Exception ex) {
                    System.out.println("ERROR: " + ex.getMessage());
                }

                while(scheduleOption <=0 || scheduleOption > tableNamesArrayList.size()) {
                    System.out.println("WRONG OPTION\n" +
                            "Which schedule would you like to " + displayOption + "?");
                    for (int j = 0; j < tableNamesArrayList.size(); j++) {
                        System.out.print("(" + (j + 1) + ") ");
                        System.out.println(tableNamesArrayList.get(j));
                    }

                    try {
                        scheduleOption = keyboard.nextInt();
                    } catch (Exception ex) {
                        System.out.println("ERROR: " + ex.getMessage());
                    }
                }

            } else {
                System.out.println("No schedules available.");
            }

            customScheduleConn.close();
            customScheduleDBMetaTables.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return scheduleOption;
    }

    public static void saveClass(Scanner keyboard, Statement announcerStatement) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        int classID;
        int scheduleOption = 0;
        boolean addClassSucessful;
        ArrayList<String> tableNamesArrayList = new ArrayList<String>();

        try {
            Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

            Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            System.out.println("Which class would you like to add to your schedule?\n" +
                    "Enter the Class ID of the course you would like to add:");
            classID = keyboard.nextInt();

            DatabaseMetaData customScheduleDBMeta = customScheduleConn.getMetaData();
            ResultSet customScheduleDBMetaTables = customScheduleDBMeta.getTables(null, null, "%", new String[] {"TABLE"});
            if (customScheduleDBMetaTables.next()) {
                System.out.println("Which schedule would you like to add the class to?");
                int i = 1;
                do {
                    System.out.print("(" + i + ") " );
                    tableNamesArrayList.add(customScheduleDBMetaTables.getString(3));
                    System.out.println(tableNamesArrayList.get(i - 1));
                    i++;
                } while (customScheduleDBMetaTables.next());
                System.out.println("(" + (i) + ") " +
                        "Create new schedule");
                try {//todo check user input if incorrect
                    scheduleOption = keyboard.nextInt();
                } catch (Exception ex) {
                    System.out.println("ERROR: " + ex.getMessage());
                }
                if (scheduleOption != i) {
                    addClassSucessful = addCourse(classID, tableNamesArrayList.get(scheduleOption - 1),
                            announcerStatement, customScheduleStatement);
                    //todo if (addClassSuccessful)
                    viewSchedule(tableNamesArrayList.get(scheduleOption - 1));
                } else {
                    System.out.println("Creating new schedule");
                    String scheduleName = createTable(keyboard, customScheduleStatement);
                    addClassSucessful = addCourse(classID, scheduleName,
                            announcerStatement, customScheduleStatement);
                    //todo if (addClassSuccessful)
                    viewSchedule(scheduleName);
                }
            } else {
                System.out.println("No schedule found... Creating new schedule");
                String scheduleName = createTable(keyboard, customScheduleStatement);
                addClassSucessful = addCourse(classID, scheduleName,
                        announcerStatement, customScheduleStatement);
                //todo if (addClassSuccessful)
                viewSchedule(scheduleName);
            }

            customScheduleConn.close();
            customScheduleDBMetaTables.close();
            customScheduleStatement.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean addCourse(int classID, String tableName,
                                    Statement announcerStatement, Statement customScheduleStatement) {
        final int COURSE_LIMIT = 7;
        final int LAST_BLOCK = 8;


        boolean courseLimitReached = false;
        boolean sameBlock = false;
        int numCourses;


        String courseLimitCheck = "SELECT COUNT(*) FROM \"" + tableName + "\" WHERE block <= " + LAST_BLOCK;
        String sameBlockCheck = "SELECT block FROM \"" + tableName + "\"";

        try {
            ResultSet courseLimitCheckRS = customScheduleStatement.executeQuery(courseLimitCheck);
            courseLimitCheckRS.next();
            numCourses = courseLimitCheckRS.getInt(1);
            if (numCourses >= COURSE_LIMIT) {
                courseLimitReached = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String getCourseSQLString = "SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk AND " +
                "class_id = " + classID;
        try {
            ResultSet courseResultSet = announcerStatement.executeQuery(getCourseSQLString);
            courseResultSet.next();
            int courseBlock = courseResultSet.getInt(7);

            ResultSet sameBlockCheckRS = customScheduleStatement.executeQuery(sameBlockCheck);
            while (sameBlockCheckRS.next()) {
                if (sameBlockCheckRS.getInt(1) == courseBlock) {
                    sameBlock = true;
                }
            }

            if (!courseLimitReached && !sameBlock) {
                String addCourseSQLSTRING = "INSERT INTO \"" + tableName + "\" VALUES (" +
                        courseResultSet.getInt(1) + ", " +
                        "'" + courseResultSet.getString(2) + "', " +
                        "'" + courseResultSet.getString(3) + "', " +
                        courseResultSet.getInt(4) + ", " +
                        courseResultSet.getInt(5) + ", " +
                        "'" + courseResultSet.getString(6).charAt(0) + "', " +
                        courseResultSet.getInt(7) + ", " +
                        "'" + courseResultSet.getString(8) + "', " +
                        "'" + courseResultSet.getString(9) + "')";
                customScheduleStatement.executeUpdate(addCourseSQLSTRING);
                return true;
            } else {
                System.out.print("Cannot add course. ");
                if (courseLimitReached) {
                    System.out.println("Course limit reached.");
                } else {
                    System.out.println("A course with the same block already exists in the schedule.");
                }
                return false;
            }

        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        return false;
    }

    public static void deleteClass(Scanner keyboard, String tableName) {
        int classID;
        boolean deleteClassSuccessful;

        try {
            viewSchedule(tableName);
            System.out.println("Which class would you like to delete?\n" +
                    "Enter the Class ID of the course you would like to delete:");
            classID = keyboard.nextInt();

            deleteClassSuccessful = deleteCourse(classID, tableName);
            //todo if (deleteClassSuccessful), wrong classID?
            if (deleteClassSuccessful) {
                viewSchedule(tableName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    //todo rename createSchedule
    public static String createTable(Scanner keyboard, Statement customScheduleStatement)  {
        System.out.println("Enter the new schedule name:");
        keyboard.nextLine();
        String scheduleName = keyboard.nextLine();
//todo handle if same schedule name
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

        try {
            customScheduleStatement.execute(createTableSQLString);
            System.out.println("Schedule created.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return scheduleName;
    }

    public static void deleteSchedule(String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        try {

            Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

            Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            String dropTableSQLString = "DROP TABLE \"" + tableName + "\"";

            customScheduleStatement.executeUpdate(dropTableSQLString);
            System.out.println(tableName + "deleted.");

            customScheduleConn.close();
            customScheduleStatement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void viewSchedule(String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        try {

            Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

            Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            String sqlStatement = "SELECT subject_id, course_id, " +
                    "course_title, class_id, " +
                    "seats, code, block, room, teacher " +
                    "FROM \"" + tableName + "\"" +
                    "ORDER BY block";
            ResultSet customScheduleResultSet = customScheduleStatement.executeQuery(sqlStatement);
            ResultSetMetaData customScheduleRSMeta = customScheduleResultSet.getMetaData();
            printSchedule(customScheduleResultSet, customScheduleRSMeta);

            customScheduleConn.close();
            customScheduleResultSet.close();
            customScheduleStatement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void printSchedule(ResultSet resultSet, ResultSetMetaData RSMetaData) {
        try {
            int courseTitleFormatWidth = RSMetaData.getColumnName(3).length() + 1; //Store the default width of the course title
            int roomFormatWidth = RSMetaData.getColumnName(8).length() + 1;//Store the default width of the room

            /* If one of the course titles has a longer name, store it for the width */
            while (resultSet.next()) {
                if (resultSet.getString(3).length() + 1 > courseTitleFormatWidth) {
                    courseTitleFormatWidth = resultSet.getString(3).length() + 1;
                }
            }

            /* Roll back the resultSet */
            resultSet.beforeFirst();

                /* If one of the rooms has a longer name, store it for the width */
            while (resultSet.next()) {
                if (resultSet.getString(8).length() + 1 > roomFormatWidth) {
                    roomFormatWidth = resultSet.getString(8).length() + 1;
                }
            }

            /* Roll back the resultSet */
            resultSet.first();

            /* Print out the column titles */
            for (int i = 1; i <= RSMetaData.getColumnCount(); i++) {
                switch (i) {
                    case 1:
                        System.out.printf("%-11s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 2:
                        System.out.printf("%-10s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 3:
                        System.out.printf("%-" + courseTitleFormatWidth + "s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 4:
                        System.out.printf("%-9s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 5:
                        System.out.printf("%-6s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 6:
                        System.out.printf("%-5s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 7:
                        System.out.printf("%-6s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 8:
                        System.out.printf("%-" + roomFormatWidth + "s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 9:
                        System.out.print(RSMetaData.getColumnName(i));
                        break;
                }
            }

            System.out.println();

                /* Print out the database results */
            do {
                for (int i = 1; i <= RSMetaData.getColumnCount(); i++) {
                    switch (i) {
                        case 1:
                            System.out.printf("%-11s", resultSet.getString(i) + " ");
                            break;
                        case 2:
                            System.out.printf("%-10s", resultSet.getString(i) + " ");
                            break;
                        case 3:
                            System.out.printf("%-" + courseTitleFormatWidth + "s", resultSet.getString(i) + " ");
                            break;
                        case 4:
                            System.out.printf("%-9s", resultSet.getString(i) + " ");
                            break;
                        case 5:
                            System.out.printf("%-6s", resultSet.getString(i) + " ");
                            break;
                        case 6:
                            System.out.printf("%-5s", resultSet.getString(i) + " ");
                            break;
                        case 7:
                            System.out.printf("%-6s", resultSet.getString(i) + " ");
                            break;
                        case 8:
                            System.out.printf("%-" + roomFormatWidth + "s", resultSet.getString(i) + " ");
                            break;
                        case 9:
                            System.out.print(resultSet.getString(i));
                            break;
                    }
                }
                System.out.println();
            } while (resultSet.next());

        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

    }
}
