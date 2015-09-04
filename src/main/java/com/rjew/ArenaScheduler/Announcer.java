package com.rjew.ArenaScheduler;

import java.sql.SQLException;
import java.util.*;

public class Announcer {
    private final static String ANNOUNCER_DB_URL = "jdbc:derby:Announcer_Fall_2015"; //For the db connection
    private static final Map<Integer , String> SUBJECT_ID = new HashMap<Integer , String>() {{
        put(1, "Math");
        put(2, "Science");
        put(3, "English");
        put(4, "SocialSci");
        put(5, "VPA");
        put(6, "WorldLang");
        put(7, "PE");
        put(8, "Other");
    }};

    public void printFullAnnouncer() throws SQLException {
        String printFullAnnouncerSQLString = "SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk";

        DAOManager daoManager = new DAOManager(ANNOUNCER_DB_URL);

        List<Course> courseList = daoManager.executeSelectQuery(printFullAnnouncerSQLString);

        //Print out number of rows
        System.out.println("\n" + DAOUtils.getResultCount(courseList) + " RESULTS.\n");

        DAOUtils.printSchedule(courseList);
    }

    public void runSearchCatalog(Scanner keyboard) throws SQLException {
        int switchOption; //To hold the option for the right switch statement case
        int menuOption; // To hold the users option based on the displayed menu
        StringBuilder sqlStatement = new StringBuilder("SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk");

        ArrayList<Object> preparedStatementParameters = new ArrayList<>();

        ArrayList<String> menuItems = new ArrayList<>(); //To create a dynamic menu based on previous choices
        menuItems.add("1Search by SubjectID");
        menuItems.add("2Search by CourseID");
        menuItems.add("3Search by CourseTitle");
        menuItems.add("4Search by ClassID");
        menuItems.add("5Search by Block");
        menuItems.add("6Search by Teacher");

        /* Print the menu */
        do {
            do {

                displaySearchCatalogMenu(menuItems);

                menuOption = ScannerUtils.getInt(keyboard);

                if (menuOption < 1 || menuOption > menuItems.size()) { //If the user's choice is out of bounds
                    System.out.println("\nWRONG OPTION!");
                }

            } while (menuOption < 1 || menuOption > menuItems.size());

            /* Convert the user's menu option to the correct switch case */
            switchOption = Character.getNumericValue(menuItems.get(menuOption - 1).charAt(0));

            getSearchCatalogQuery(keyboard, switchOption, sqlStatement, preparedStatementParameters);

            menuItems.remove(menuOption - 1); //Remove the menu option that the user picked from the display

            /* Prompt the user for another search parameter */
            if (menuItems.size() != 0) { //Check to see if all parameters are already taken

                do {

                    displayExtraParameterMenu();

                    switchOption = ScannerUtils.getInt(keyboard);

                    if (switchOption < 1 || switchOption > 2) { //Out of bounds option
                        System.out.println("\nWRONG OPTION!");
                    }

                } while (switchOption < 1 || switchOption > 2);

            } else { //Exit menu if all parameters are taken
                switchOption = 2;
            }

        } while(switchOption == 1);

        executeSQLStatement(sqlStatement.toString(), preparedStatementParameters);
    }

    /**
     * Prints the search catalog menu
     * @param menuItems An ArrayList holding the possible search parameters to use
     */
    private void displaySearchCatalogMenu(ArrayList<String> menuItems) {

        System.out.println("\nWhat would you like to search for?");

        for(int i = 1; i <= menuItems.size(); i++) { //Print all available menu items
            System.out.println("(" + i + ") " + menuItems.get(i-1).substring(1));
        }

    }

    /**
     * Gets user's input for the search parameter and builds the query from it
     * @param keyboard For user input
     * @param switchOption The option corresponding to the correct switch statement
     * @param sqlStatement A StringBuilder that holds the search query based on the user's input parameters
     * @param preparedStatementParameters
     */
    private void getSearchCatalogQuery(Scanner keyboard, int switchOption,
                                       StringBuilder sqlStatement, ArrayList<Object> preparedStatementParameters) {
        String searchOptionString; //To hold user input for search catalog for strings
        int searchOptionInt; //To hold user input for search catalog for ints

        /* Give menu options */
        switch (switchOption) {
            case 1:
                System.out.println("\nFor your reference:");
                System.out.println(SUBJECT_ID);

                do {
                    System.out.print("Enter the Subject ID: ");
                    searchOptionInt = ScannerUtils.getInt(keyboard);
                    if (searchOptionInt < 1 || searchOptionInt > 8) {
                        System.out.println("\nWRONG OPTION!\n");
                    }
                } while (searchOptionInt < 1 || searchOptionInt > 8);
                sqlStatement.append(" AND subject_id = ?");
                preparedStatementParameters.add(searchOptionInt);
                break;
            case 2:
                System.out.print("\nEnter the Course ID: ");
                searchOptionString = ScannerUtils.getString(keyboard);
                sqlStatement.append(" AND LOWER(course_id_fk) LIKE ?");
                preparedStatementParameters.add(searchOptionString.toLowerCase());
                break;
            case 3:
                System.out.print("\nEnter the Course Title: ");
                searchOptionString = ScannerUtils.getString(keyboard);
                sqlStatement.append(" AND LOWER(course_title_uq) LIKE ?");
                preparedStatementParameters.add("%" + searchOptionString.toLowerCase() + "%");
                break;
            case 4:
                do {
                    System.out.print("\nEnter the Class ID: ");
                    searchOptionInt = ScannerUtils.getInt(keyboard);
                    if (searchOptionInt < 1) {
                        System.out.println("\nWRONG OPTION!");
                    }
                } while (searchOptionInt < 1);
                sqlStatement.append(" AND class_id = ?");
                preparedStatementParameters.add(searchOptionInt);
                break;
            case 5:
                do {
                    System.out.print("\nEnter the Block: ");
                    searchOptionInt = ScannerUtils.getInt(keyboard);
                    if (searchOptionInt < 1) {
                        System.out.println("\nWRONG OPTION!");
                    }
                } while (searchOptionInt < 1);
                sqlStatement.append(" AND block = ?");
                preparedStatementParameters.add(searchOptionInt);
                break;
            case 6:
                System.out.print("\nEnter the Teacher: ");
                searchOptionString = ScannerUtils.getString(keyboard);
                sqlStatement.append(" AND LOWER(teacher) LIKE ?");
                preparedStatementParameters.add("%" + searchOptionString.toLowerCase() + "%");
                break;
        }
    }

    /**
     * Prints the menu to choose another search parameter
     */
    private void displayExtraParameterMenu() {
        System.out.println("\nWould you like to add another search parameter?\n" +
                "(1) Yes\n" +
                "(2) No");
    }

    /**
     * Executes sql statement based on the parameters given in the search catalog
     * @param sqlStmt A String holding the sql statement to be executed
     * @param preparedStatementParameters
     */
    private void executeSQLStatement(String sqlStmt, ArrayList<Object> preparedStatementParameters) throws SQLException {
        int numRows; //To hold the number of rows, the number of results

        DAOManager daoManager = new DAOManager(ANNOUNCER_DB_URL);

        List<Course> courseList = daoManager.executeSearchCatalogQuery(sqlStmt, preparedStatementParameters);

        numRows = DAOUtils.getResultCount(courseList);

        System.out.println("\n" + numRows + " RESULTS.\n");

        if (numRows != 0) {
            DAOUtils.printSchedule(courseList);
        }
    }

    public Course getCourse(Scanner keyboard) throws SQLException {
        int classID;

        do {
            System.out.println("Enter the ID of the course that you would like:");
            classID = ScannerUtils.getInt(keyboard);

            if (classID < 1) {
                System.out.println("\nWRONG OPTION!");
            }
        } while (classID < 1);

        DAOManager daoManager = new DAOManager(ANNOUNCER_DB_URL);
        return daoManager.getCourse(classID);
    }
}
