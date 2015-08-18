package com.rjew.ArenaScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SearchCatalog {
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

    public static void displaySearchCatalog(Scanner keyboard) {
        int switchOption; //To hold the option for the right switch statement case
        int menuOption; // To hold the users option based on the displayed menu
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

                displaySearchCatalogMenu(menuItems);

                menuOption = getSearchCatalogMenuOption(keyboard, menuItems);

            } while (menuOption < 1 || menuOption > menuItems.size());

            /* Convert the user's menu option to the correct switch case */
            switchOption = Character.getNumericValue(menuItems.get(menuOption - 1).charAt(0));

            getSearchCatalogQuery(keyboard, switchOption, sqlStatement);

            menuItems.remove(menuOption - 1); //Remove the menu option that the user picked from the display

            /* Prompt the user for another search parameter */
            if (menuItems.size() != 0) { //Check to see if all parameters are already taken

                do {

                    displayExtraParameterMenu();

                    switchOption = getExtraParameterMenuOption(keyboard);

                } while (switchOption < 1 || switchOption > 2);

            } else { //Exit menu if all parameters are taken
                switchOption = 2;
            }

        } while(switchOption == 1);

        ExecuteSQL.executeSQLStatement(sqlStatement.toString(), keyboard);
    }

    public static void displaySearchCatalogMenu(ArrayList<String> menuItems) {

        System.out.println("What would you like to search for?");

        for(int i = 1; i <= menuItems.size(); i++) { //Print all available menu items
            System.out.println("(" + i + ") " + menuItems.get(i-1).substring(1));
        }

    }

    public static int getSearchCatalogMenuOption(Scanner keyboard, ArrayList<String> menuItems) {
        int menuOption = 0;

        try {
            menuOption = keyboard.nextInt();
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        if (menuOption < 1 || menuOption > menuItems.size()) { //If the user's choice is out of bounds
            System.out.println("WRONG OPTION!");
        }

        return menuOption;
    }

    public static void getSearchCatalogQuery(Scanner keyboard, int switchOption, StringBuilder sqlStatement) {
        String searchOptionString; //To hold user input for search catalog for strings
        int searchOptionInt; //To hold user input for search catalog for ints

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
    }

    public static void displayExtraParameterMenu() {
        System.out.println("Would you like to add another search parameter?\n" +
                "(1) Yes\n" +
                "(2) No");
    }

    public static int getExtraParameterMenuOption(Scanner keyboard) {
        int switchOption = 0;

        try {
            switchOption = keyboard.nextInt();
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
        if (switchOption < 1 || switchOption > 2) { //Out of bounds option
            System.out.println("WRONG OPTION!");
        }

        return switchOption;
    }
}
