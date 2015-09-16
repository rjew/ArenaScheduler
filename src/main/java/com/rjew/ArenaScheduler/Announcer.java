package com.rjew.ArenaScheduler;

import java.sql.SQLException;
import java.util.*;

/**
 * Concerned with collecting the necessary user input in order to
 * execute the specified daoManager method for the announcer
 * and sends output to the user about the query
 */
class Announcer {
    private static final String ANNOUNCER_DB_URL = "jdbc:derby:Announcer_Fall_2015"; //For the db connection
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

    private final DAOManager daoManager;

    /**
     * Initializes the daoManager field
     */
    public Announcer() {
        daoManager = new DAOManager(ANNOUNCER_DB_URL);
    }

    /**
     * Prints the complete announcer
     * @throws SQLException
     */
    public void printFullAnnouncer() throws SQLException {
        String printFullAnnouncerSQLString = "SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk";

        List<Course> courseList = daoManager.executeSelectQuery(printFullAnnouncerSQLString);

        //Print out number of rows
        System.out.println("\n" + DAOUtils.getCourseCount(courseList) + " RESULTS.\n");

        DAOUtils.printCourseList(courseList);
    }

    /**
     * Runs the search catalog for the announcer
     * @return An int holding the number of results, for the case when the number of results is 0
     * @throws SQLException
     */
    public int runSearchCatalog() throws SQLException {
        int switchOption; //To hold the option for the right switch statement case
        int menuOption; // To hold the users option based on the displayed menu
        StringBuilder sqlStatement = new StringBuilder("SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk");

        List<Object> preparedStatementParameters = new ArrayList<>();

        List<String> menuItems = new ArrayList<>(); //To create a dynamic menu based on previous choices
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

                menuOption = ScannerUtils.getInt();

                if (menuOption < 1 || menuOption > menuItems.size()) { //If the user's choice is out of bounds
                    System.out.println("\nWRONG OPTION!");
                }

            } while (menuOption < 1 || menuOption > menuItems.size());

            /* Convert the user's menu option to the correct switch case */
            switchOption = Character.getNumericValue(menuItems.get(menuOption - 1).charAt(0));

            getSearchCatalogQuery(switchOption, sqlStatement, preparedStatementParameters);

            menuItems.remove(menuOption - 1); //Remove the menu option that the user picked from the display

            /* Prompt the user for another search parameter */
            if (menuItems.size() != 0) { //Check to see if all parameters are already taken

                do {

                    displayExtraParameterMenu();

                    switchOption = ScannerUtils.getInt();

                    if (switchOption < 1 || switchOption > 2) { //Out of bounds option
                        System.out.println("\nWRONG OPTION!");
                    }

                } while (switchOption < 1 || switchOption > 2);

            } else { //Exit menu if all parameters are taken
                switchOption = 2;
            }

        } while(switchOption == 1);

        return executeSQLStatement(sqlStatement.toString(), preparedStatementParameters);
    }

    /**
     * Displays the possible search catalog criteria
     * @param menuItems A List holding the remaining search catalog parameters
     */
    private void displaySearchCatalogMenu(List<String> menuItems) {

        System.out.println("\nWhat would you like to search for?");

        for(int i = 1; i <= menuItems.size(); i++) { //Print all available menu items
            System.out.println("(" + i + ") " + menuItems.get(i-1).substring(1));
        }

    }

    /**
     * Get the user's input to create a search catalog query
     * @param switchOption The option corresponding to the correct switch statement
     * @param sqlStatement The SQL query to be executed
     * @param preparedStatementParameters The parameters to be inserted into the SQL query
     */
    private void getSearchCatalogQuery(int switchOption, StringBuilder sqlStatement,
                                       List<Object> preparedStatementParameters) {
        String searchOptionString; //To hold user input for search catalog for strings
        int searchOptionInt; //To hold user input for search catalog for ints

        /* Give menu options */
        switch (switchOption) {
            case 1:
                System.out.println("\nFor your reference:");
                System.out.println(SUBJECT_ID);

                do {
                    System.out.print("Enter the Subject ID: ");
                    searchOptionInt = ScannerUtils.getInt();
                    if (searchOptionInt < 1 || searchOptionInt > 8) {
                        System.out.println("\nWRONG OPTION!\n");
                    }
                } while (searchOptionInt < 1 || searchOptionInt > 8);
                sqlStatement.append(" AND subject_id = ?");
                preparedStatementParameters.add(searchOptionInt);
                break;
            case 2:
                System.out.print("\nEnter the Course ID: ");
                searchOptionString = ScannerUtils.getString();
                sqlStatement.append(" AND LOWER(course_id_fk) LIKE ?");
                preparedStatementParameters.add(searchOptionString.toLowerCase());
                break;
            case 3:
                System.out.print("\nEnter the Course Title: ");
                searchOptionString = ScannerUtils.getString();
                sqlStatement.append(" AND LOWER(course_title_uq) LIKE ?");
                preparedStatementParameters.add("%" + searchOptionString.toLowerCase() + "%");
                break;
            case 4:
                do {
                    System.out.print("\nEnter the Class ID: ");
                    searchOptionInt = ScannerUtils.getInt();
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
                    searchOptionInt = ScannerUtils.getInt();
                    if (searchOptionInt < 1) {
                        System.out.println("\nWRONG OPTION!");
                    }
                } while (searchOptionInt < 1);
                sqlStatement.append(" AND block = ?");
                preparedStatementParameters.add(searchOptionInt);
                break;
            case 6:
                System.out.print("\nEnter the Teacher: ");
                searchOptionString = ScannerUtils.getString();
                sqlStatement.append(" AND LOWER(teacher) LIKE ?");
                preparedStatementParameters.add("%" + searchOptionString.toLowerCase() + "%");
                break;
        }
    }

    /**
     * Menu asking for another parameter
     */
    private void displayExtraParameterMenu() {
        System.out.println("\nWould you like to add another search parameter?\n" +
                "(1) Yes\n" +
                "(2) No");
    }

    /**
     * Executes the given search catalog query and displays the results
     * @param sqlStatement The SQL query to be executed
     * @param preparedStatementParameters The parameters of the search query to be inserted into the SQL statement
     * @return An int holding the number of results from the query
     * @throws SQLException
     */
    private int executeSQLStatement(String sqlStatement, List<Object> preparedStatementParameters) throws SQLException {
        int numRows; //To hold the number of rows, the number of results

        List<Course> courseList = daoManager.executeSearchCatalogQuery(sqlStatement, preparedStatementParameters);

        numRows = DAOUtils.getCourseCount(courseList);

        System.out.println("\n" + numRows + " RESULTS.\n");

        if (numRows != 0) {
            DAOUtils.printCourseList(courseList);
        }

        return numRows;
    }

    /**
     * Gets the classID of a course from the user and returns a Course object
     * @return A Course object that the user specifies
     * @throws SQLException
     */
    public Course getCourse() throws SQLException {
        int classID;

        do {
            System.out.println("\nWhich class would you like to add to your schedule?\n" +
                    "Enter the Class ID of the course you would like to add:");
            classID = ScannerUtils.getInt();
            if (classID < 1) {
                System.out.println("\nWRONG OPTION!");
            }
        } while (classID < 1);

        return daoManager.getCourse(classID);
    }
}
