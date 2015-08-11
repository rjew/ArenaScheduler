package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.*;

public class ArenaScheduler {
    static final Map<Integer , String> SUBJECT_ID = new HashMap<Integer , String>() {{
        put(1, "Math");
        put(2, "Science");
        put(3, "English");
        put(4, "SocialSci");
        put(5, "VPA");
        put(6, "WorldLang");
        put(7, "PE");
        put(8, "Other");
    }};

    public static void main(String[] args ) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

            displayArenaSchedulerMenu();
        }
        catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    public static void displayArenaSchedulerMenu() {
        int option = 0;

        Scanner keyboard = new Scanner(System.in);

        /* Print the Menu */
        do {
            System.out.println("Welcome to the Arena Scheduler Program!\n"
                    + "What would you like to do?\n"
                    + "(1) Search Catalog\n"
                    + "(2) View your custom schedules\n"
                    + "(3) Quit\n"
                    + "Pick an option (1-3) and press ENTER.");//todo View full announcer - allow add class to schedule
            try {
                option = keyboard.nextInt();
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }

            switch (option) {
                case 1:
                    displaySearchCatalog(keyboard);
                    break;
                case 2:
                    accessCustomSchedules(keyboard);
                    break;
                case 3:
                    break;
                default:
                    System.out.println("WRONG OPTION!");
            }
        } while (option != 3);

        keyboard.close();
    }

    public static void displaySearchCatalog(Scanner keyboard) {
        int switchOption; //To hold the option for the right switch statement case
        int menuOption = 0; // To hold the users option based on the displayed menu
        String searchOptionString; //To hold user input for search catalog for strings
        int searchOptionInt; //To hold user input for search catalog for ints
        StringBuilder sqlStatement = new StringBuilder("SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk");

        ArrayList<String> menuItems = new ArrayList<String>(); //To create a dynamic menu based on previous choices
        menuItems.add("1Search by SubjectID");
        menuItems.add("2Search by CourseID");
        menuItems.add("3Search by CourseTitle");
        menuItems.add("4Search by ClassID");
        menuItems.add("5Search by Block");
        menuItems.add("6Search by Teacher");

        /* Print the menu */
        do {
            do {
                System.out.println("What would you like to search for?");
                for(int i = 1; i <= menuItems.size(); i++) { //Print all available menu items
                    System.out.println("(" + i + ") " + menuItems.get(i-1).substring(1));
                }
                try {
                    menuOption = keyboard.nextInt();
                } catch (Exception ex) {
                    System.out.println("ERROR: " + ex.getMessage());
                }

                if (menuOption < 1 || menuOption > menuItems.size()) { //If the user's choice is out of bounds
                    System.out.println("WRONG OPTION!");
                }
            } while (menuOption < 1 || menuOption > menuItems.size());

            /* Convert the user's menu option to the correct switch case */
            switchOption = Character.getNumericValue(menuItems.get(menuOption - 1).charAt(0));

            /* Give menu options */
            switch (switchOption) {
                case 1:
                    System.out.println("For your reference:");
                    System.out.println(SUBJECT_ID);
                    System.out.print("Enter the Subject ID: ");
                    searchOptionInt = keyboard.nextInt();
                    sqlStatement.append(" AND subject_id = ");
                    sqlStatement.append(searchOptionInt);
                    break;
                case 2:
                    System.out.print("Enter the Course ID: ");
                    keyboard.nextLine(); //Consume the newline
                    searchOptionString = keyboard.nextLine();
                    sqlStatement.append(" AND LOWER(course_id_fk) LIKE LOWER('");
                    sqlStatement.append(searchOptionString);
                    sqlStatement.append("')");
                    break;
                case 3:
                    System.out.print("Enter the Course Title: ");
                    keyboard.nextLine(); //Consume the newline
                    searchOptionString = keyboard.nextLine();
                    sqlStatement.append(" AND LOWER(course_title_uq) LIKE LOWER('%");
                    sqlStatement.append(searchOptionString);
                    sqlStatement.append("%')");
                    break;
                case 4:
                    System.out.print("Enter the Class ID: ");
                    searchOptionInt = keyboard.nextInt();
                    sqlStatement.append(" AND class_id = ");
                    sqlStatement.append(searchOptionInt);
                    break;
                case 5:
                    System.out.print("Enter the Block: ");
                    searchOptionInt = keyboard.nextInt();
                    sqlStatement.append(" AND block = ");
                    sqlStatement.append(searchOptionInt);
                    break;
                case 6:
                    System.out.print("Enter the Teacher: ");
                    keyboard.nextLine(); //Consume the newline
                    searchOptionString = keyboard.nextLine();
                    sqlStatement.append(" AND LOWER(teacher) LIKE LOWER('%");
                    sqlStatement.append(searchOptionString);
                    sqlStatement.append("%')");
                    break;
                }

            menuItems.remove(menuOption - 1); //Remove the menu option that the user picked from the display

            /* Prompt the user for another search parameter */
            if (menuItems.size() != 0) { //Check to see if all parameters are already taken
                do {
                    System.out.println("Would you like to add another search parameter?\n" +
                            "(1) Yes\n" +
                            "(2) No");
                    try {
                        switchOption = keyboard.nextInt();
                    } catch (Exception ex) {
                        System.out.println("ERROR: " + ex.getMessage());
                    }
                    if (switchOption < 1 || switchOption > 2) { //Out of bounds option
                        System.out.println("WRONG OPTION!");
                    }
                } while (switchOption < 1 || switchOption > 2);
            } else { //Exit menu if all parameters are taken
                switchOption = 2;
            }
        } while(switchOption == 1);

        executeSQLStatement(sqlStatement.toString(), keyboard);
    }

    public static void executeSQLStatement(String sqlStmt, Scanner keyboard) {
        final String ANNOUNCER_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Announcer_Fall_2015"; //For the db connection
        int numRows; //To hold the number of rows, the number of results
        int addClassOption = 0; // To hold the option for adding a class to the custom schedule

        try {

            /* Connect to database and execute query, storing the results */
            Connection announcerConn = DriverManager.getConnection(ANNOUNCER_DB_URL);

            Statement announcerStatement = announcerConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet announcerResultSet = announcerStatement.executeQuery(sqlStmt);

            ResultSetMetaData announcerRSMeta = announcerResultSet.getMetaData();

            /* Get the number of results */
            announcerResultSet.last();
            numRows = announcerResultSet.getRow();
            announcerResultSet.first();

            System.out.println("\n" + numRows + " RESULTS.\n");

            if (numRows != 0) {
                int courseTitleFormatWidth = announcerRSMeta.getColumnName(3).length() + 1; //Store the default width of the course title
                int roomFormatWidth = announcerRSMeta.getColumnName(8).length() + 1;//Store the default width of the room

                /* If one of the course titles has a longer name, store it for the width */
                do {
                    if (announcerResultSet.getString(3).length() + 1 > courseTitleFormatWidth) {
                        courseTitleFormatWidth = announcerResultSet.getString(3).length() + 1;
                    }
                } while (announcerResultSet.next());

                /* Roll back the resultSet */
                announcerResultSet.first();

                /* If one of the rooms has a longer name, store it for the width */
                do {
                    if (announcerResultSet.getString(8).length() + 1 > roomFormatWidth) {
                        roomFormatWidth = announcerResultSet.getString(8).length() + 1;
                    }
                } while (announcerResultSet.next());

                /* Roll back the resultSet */
                announcerResultSet.first();

                /* Print out the column titles */
                for (int i = 1; i <= announcerRSMeta.getColumnCount(); i++) {
                    switch (i) {
                        case 1:
                            System.out.printf("%-11s", announcerRSMeta.getColumnName(i) + " ");
                            break;
                        case 2:
                            System.out.printf("%-10s", announcerRSMeta.getColumnName(i) + " ");
                            break;
                        case 3:
                            System.out.printf("%-" + courseTitleFormatWidth + "s", announcerRSMeta.getColumnName(i) + " ");
                            break;
                        case 4:
                            System.out.printf("%-9s", announcerRSMeta.getColumnName(i) + " ");
                            break;
                        case 5:
                            System.out.printf("%-6s", announcerRSMeta.getColumnName(i) + " ");
                            break;
                        case 6:
                            System.out.printf("%-5s", announcerRSMeta.getColumnName(i) + " ");
                            break;
                        case 7:
                            System.out.printf("%-6s", announcerRSMeta.getColumnName(i) + " ");
                            break;
                        case 8:
                            System.out.printf("%-" + roomFormatWidth + "s", announcerRSMeta.getColumnName(i) + " ");
                            break;
                        case 9:
                            System.out.print(announcerRSMeta.getColumnName(i));
                            break;
                    }
                }

                System.out.println();

                /* Print out the database results */
                do {
                    for (int i = 1; i <= announcerRSMeta.getColumnCount(); i++) {
                        switch (i) {
                            case 1:
                                System.out.printf("%-11s", announcerResultSet.getString(i) + " ");
                                break;
                            case 2:
                                System.out.printf("%-10s", announcerResultSet.getString(i) + " ");
                                break;
                            case 3:
                                System.out.printf("%-" + courseTitleFormatWidth + "s", announcerResultSet.getString(i) + " ");
                                break;
                            case 4:
                                System.out.printf("%-9s", announcerResultSet.getString(i) + " ");
                                break;
                            case 5:
                                System.out.printf("%-6s", announcerResultSet.getString(i) + " ");
                                break;
                            case 6:
                                System.out.printf("%-5s", announcerResultSet.getString(i) + " ");
                                break;
                            case 7:
                                System.out.printf("%-6s", announcerResultSet.getString(i) + " ");
                                break;
                            case 8:
                                System.out.printf("%-" + roomFormatWidth + "s", announcerResultSet.getString(i) + " ");
                                break;
                            case 9:
                                System.out.print(announcerResultSet.getString(i));
                                break;
                        }
                    }
                    System.out.println();
                } while (announcerResultSet.next());

                do {
                    System.out.println("Would you like to add any of the classes to one of your schedules?\n" +
                            "(1) Yes\n" +
                            "(2) No");
                    try {
                        addClassOption = keyboard.nextInt();
                    } catch (Exception ex) {
                        System.out.println("ERROR: " + ex.getMessage());
                    }
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
                try {
                    scheduleOption = keyboard.nextInt();
                } catch (Exception ex) {
                    System.out.println("ERROR: " + ex.getMessage());
                }
                if (scheduleOption != i) {
                    addClassSucessful = addCourse(classID, tableNamesArrayList.get(scheduleOption - 1),
                            announcerStatement, customScheduleStatement);
                    //todo if (addClassSuccessful)
                    String sqlStatement = "SELECT subject_id, course_id, " +
                            "course_title, class_id, " +
                            "seats, code, block, room, teacher " +
                            "FROM \"" + tableNamesArrayList.get(scheduleOption - 1) + "\"" +
                            "ORDER BY block";
                    ResultSet customScheduleResultSet = customScheduleStatement.executeQuery(sqlStatement);
                    ResultSetMetaData customScheduleRSMeta = customScheduleResultSet.getMetaData();
                    printSchedule(customScheduleResultSet, customScheduleRSMeta);

                    customScheduleResultSet.close();
                } else {
                    System.out.println("Creating new schedule");
                    String scheduleName = createTable(keyboard, customScheduleStatement);
                    addClassSucessful = addCourse(classID, scheduleName,
                            announcerStatement, customScheduleStatement);
                    //todo if (addClassSuccessful)
                    String sqlStatement = "SELECT subject_id, course_id, " +
                            "course_title, class_id, " +
                            "seats, code, block, room, teacher " +
                            "FROM \"" + scheduleName + "\"" +
                            "ORDER BY block";
                    ResultSet customScheduleResultSet = customScheduleStatement.executeQuery(sqlStatement);
                    ResultSetMetaData customScheduleRSMeta = customScheduleResultSet.getMetaData();
                    printSchedule(customScheduleResultSet, customScheduleRSMeta);

                    customScheduleResultSet.close();
                }
            } else {
                System.out.println("No schedule found... Creating new schedule");
                String scheduleName = createTable(keyboard, customScheduleStatement);
                addClassSucessful = addCourse(classID, scheduleName,
                        announcerStatement, customScheduleStatement);
                //todo if (addClassSuccessful)
                String sqlStatement = "SELECT subject_id, course_id, " +
                        "course_title, class_id, " +
                        "seats, code, block, room, teacher " +
                        "FROM \"" + scheduleName + "\"" +
                        "ORDER BY block";
                ResultSet customScheduleResultSet = customScheduleStatement.executeQuery(sqlStatement);
                ResultSetMetaData customScheduleRSMeta = customScheduleResultSet.getMetaData();
                printSchedule(customScheduleResultSet, customScheduleRSMeta);

                customScheduleResultSet.close();
            }

            customScheduleConn.close();
            customScheduleDBMetaTables.close();
            customScheduleStatement.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
//todo add sort by block to sql statement
    public static void printSchedule(ResultSet resultSet, ResultSetMetaData RSMetaData) {
        try {
            int courseTitleFormatWidth = RSMetaData.getColumnName(3).length() + 1; //Store the default width of the course title
            int roomFormatWidth = RSMetaData.getColumnName(8).length() + 1;//Store the default width of the room

            resultSet.next();

            /* If one of the course titles has a longer name, store it for the width */
            do {
                if (resultSet.getString(3).length() + 1 > courseTitleFormatWidth) {
                    courseTitleFormatWidth = resultSet.getString(3).length() + 1;
                }
            } while (resultSet.next());

            /* Roll back the resultSet */
            resultSet.first();

                /* If one of the rooms has a longer name, store it for the width */
            do {
                if (resultSet.getString(8).length() + 1 > roomFormatWidth) {
                    roomFormatWidth = resultSet.getString(8).length() + 1;
                }
            } while (resultSet.next());

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

    public static void accessCustomSchedules(Scanner keyboard) {
        int menuOption = 0;

        do {
            System.out.println("What would you like to do?\n" +
                    "(1) Add a course to a schedule\n" +
                    "(2) Delete a course from a schedule\n" +
                    "(3) Create a new schedule\n" +
                    "(4) Delete a schedule" +
                    "(5) View one of your schedules\n" +
                    "(6) Quit");
            try {
                menuOption = keyboard.nextInt();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            switch (menuOption) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    displayScheduleOptions(keyboard);
                    break;
                case 6:
                    break;
                default:
                    System.out.println("WRONG OPTION!");
            }

        } while (menuOption != 5);
    }

    public static void displayScheduleOptions(Scanner keyboard) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        int scheduleOption = 0;
        ArrayList<String> tableNamesArrayList = new ArrayList<String>();

        try {
            Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

            Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            DatabaseMetaData customScheduleDBMeta = customScheduleConn.getMetaData();
            ResultSet customScheduleDBMetaTables = customScheduleDBMeta.getTables(null, null, "%", new String[]{"TABLE"});

            if (customScheduleDBMetaTables.next()) {
                System.out.println("Which schedule would you like to view?");
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

                if (scheduleOption > 0 && scheduleOption <= tableNamesArrayList.size()) {
                    String sqlStatement = "SELECT subject_id, course_id, " +
                            "course_title, class_id, " +
                            "seats, code, block, room, teacher " +
                            "FROM \"" + tableNamesArrayList.get(scheduleOption - 1) + "\"" +
                            "ORDER BY block";
                    ResultSet customScheduleResultSet = customScheduleStatement.executeQuery(sqlStatement);
                    ResultSetMetaData customScheduleRSMeta = customScheduleResultSet.getMetaData();
                    printSchedule(customScheduleResultSet, customScheduleRSMeta);

                    customScheduleResultSet.close();
                }
            } else {
                System.out.println("No schedules available.");
            }

            customScheduleConn.close();
            customScheduleDBMetaTables.close();
            customScheduleStatement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}