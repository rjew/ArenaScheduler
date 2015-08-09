package com.rjew.ArenaScheduler;

import java.sql.*;
import java.io.*;
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
        final String DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Announcer_Fall_2015";

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

            Connection conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection created to CoffeeDB.");

            displayArenaSchedulerMenu();
        }
        catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    public static void displayArenaSchedulerMenu() {
        int option = 0;
        ArrayList<Schedule> scheduleArrayList = new ArrayList<Schedule>();

        Scanner keyboard = new Scanner(System.in);

        /* Print the Menu */
        do {
            System.out.println("Welcome to the Arena Scheduler Program!\n"
                    + "What would you like to do?\n"
                    + "(1) Search Catalog\n"
                    + "(2) View your custom schedules\n"
                    + "(3) Quit\n"
                    + "Pick an option (1-3) and press ENTER.");
            try {
                option = keyboard.nextInt();
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }

            switch (option) {
                case 1:
                    displaySearchCatalog(keyboard, scheduleArrayList);
                    break;
                case 2:
                    viewCustomSchedules(keyboard, scheduleArrayList);
                    break;
                case 3:
                    break;
                default:
                    System.out.println("WRONG OPTION!");
            }
        } while (option != 3);

        keyboard.close();
    }

    public static void displaySearchCatalog(Scanner keyboard, ArrayList<Schedule> scheduleArrayList) {
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

        executeSQLStatement(sqlStatement.toString(), keyboard, scheduleArrayList);
    }

    public static void executeSQLStatement(String sqlStmt, Scanner keyboard, ArrayList<Schedule> scheduleArrayList) {
        final String DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Announcer_Fall_2015"; //For the db connection
        int numRows; //To hold the number of rows, the number of results
        int addClassOption = 0; // To hold the option for adding a class to the custom schedule

        try {

            /* Connect to database and execute query, storing the results */
            Connection conn = DriverManager.getConnection(DB_URL);

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet resultSet = stmt.executeQuery(sqlStmt);

            ResultSetMetaData meta = resultSet.getMetaData();

            /* Get the number of results */
            resultSet.last();
            numRows = resultSet.getRow();
            resultSet.first();

            System.out.println("\n" + numRows + " RESULTS.\n");

            if (numRows != 0) {
                int courseTitleFormatWidth = meta.getColumnName(3).length() + 1; //Store the default width of the course title
                int roomFormatWidth = meta.getColumnName(8).length() + 1;//Store the default width of the room

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
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    switch (i) {
                        case 1:
                            System.out.printf("%-11s", meta.getColumnName(i) + " ");
                            break;
                        case 2:
                            System.out.printf("%-10s", meta.getColumnName(i) + " ");
                            break;
                        case 3:
                            System.out.printf("%-" + courseTitleFormatWidth + "s", meta.getColumnName(i) + " ");
                            break;
                        case 4:
                            System.out.printf("%-9s", meta.getColumnName(i) + " ");
                            break;
                        case 5:
                            System.out.printf("%-6s", meta.getColumnName(i) + " ");
                            break;
                        case 6:
                            System.out.printf("%-5s", meta.getColumnName(i) + " ");
                            break;
                        case 7:
                            System.out.printf("%-6s", meta.getColumnName(i) + " ");
                            break;
                        case 8:
                            System.out.printf("%-" + roomFormatWidth + "s", meta.getColumnName(i) + " ");
                            break;
                        case 9:
                            System.out.print(meta.getColumnName(i));
                            break;
                    }
                }

                System.out.println();

                /* Print out the database results */
                do {
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
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
                            resultSet.first();
                            saveClass(keyboard, stmt, scheduleArrayList);
                            break;
                        case 2:
                            break;
                        default:
                            System.out.println("WRONG OPTION!");
                    }
                } while (addClassOption < 1 || addClassOption > 2);
            }

            stmt.close();
            conn.close();
            System.out.println("\nConnection closed.");
        }
        catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    public static void saveClass(Scanner keyboard, Statement stmt, ArrayList<Schedule> scheduleArrayList) {
        int classID = 0;
        int scheduleOption = 0;

        System.out.println("Which class would you like to add to your schedule?\n" +
                "Enter the Class ID of the course you would like to add:");
        try {
            classID = keyboard.nextInt();
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        System.out.println("Which schedule would you like to add the class to?");
        if (scheduleArrayList.size() != 0) {
            for (int i = 0; i < scheduleArrayList.size(); i++) {
                System.out.println("(" + (i + 1) + ") " + scheduleArrayList.get(i).getName());
            }
            System.out.println("(" + (scheduleArrayList.size() + 1) + ") " +
                    "Create new schedule");
            try {
                scheduleOption = keyboard.nextInt();
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            if (scheduleOption == (scheduleArrayList.size() + 1)) {
                scheduleArrayList.add(new Schedule());
                System.out.println("Enter the new schedule name:");
                keyboard.nextLine();
                String scheduleName = keyboard.nextLine();
                scheduleArrayList.get(scheduleOption - 1).changeName(scheduleName);
            }
        } else {
            System.out.println("No schedules found... Creating new schedule\n" +
                    "Enter the new schedule name:");
            keyboard.nextLine();
            String scheduleName = keyboard.nextLine();
            scheduleArrayList.add(new Schedule());
            scheduleArrayList.get(0).changeName(scheduleName);
            scheduleOption = 1;
        }

        String sqlStatement = "SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk AND " +
                "class_id = " + classID;
        try {
            ResultSet resultSet = stmt.executeQuery(sqlStatement);

            Course course = new Course(resultSet);

            scheduleArrayList.get(scheduleOption - 1).addCourse(course);

        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

    }

    public static void viewCustomSchedules(Scanner keyboard, ArrayList<Schedule> scheduleArrayList) {
        int scheduleViewOption = 0;

        System.out.println("Which schedule would you like to see?");
        for (int i = 1; i <= scheduleArrayList.size(); i++) {
            System.out.print("(" + i + ") " + scheduleArrayList.get(i - 1).getName());
        }
        if (scheduleArrayList.size() > 1) {
            System.out.println("(" + (scheduleArrayList.size() + 1) + ") All of them");
        }
        try {
            scheduleViewOption = keyboard.nextInt();
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        if (scheduleViewOption != scheduleArrayList.size() + 1) {
            scheduleArrayList.get(scheduleViewOption - 1).sortScheduleByBlock();

            System.out.println(scheduleArrayList.get(scheduleViewOption - 1));
        } else {
            for (Schedule s : scheduleArrayList) { //For Each loop
                s.sortScheduleByBlock();

                System.out.println(s);
            }
        }

    }
}