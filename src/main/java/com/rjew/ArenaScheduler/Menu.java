package com.rjew.ArenaScheduler;

import java.sql.SQLException;

/**
 * Responsible for displaying the menu and getting user input regarding what the user wants to do
 */
public final class Menu {

    /**
     * To prevent instantiation
     */
    private Menu() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Responsible for the starting the main menu
     * @throws SQLException
     */
    public static void displayArenaSchedulerMenu() throws SQLException {
        System.out.println("Welcome to the Arena Scheduler Program!\n");

        getMainMenu();
    }

    /**
     * Responsible for the main menu
     * @throws SQLException
     */
    private static void getMainMenu() throws SQLException {
        int menuOption;
        Announcer announcer = new Announcer();

        /* Print the Menu */
        do {
            displayMainMenu();

            menuOption = ScannerUtils.getInt();

            switch (menuOption) {
                case 1:
                    if (announcer.runSearchCatalog() != 0) {
                        getAddClassAnnouncerMenu();
                    }
                    break;
                case 2:
                    getCustomScheduleMenu();
                    break;
                case 3:
                    announcer.printFullAnnouncer();
                    getAddClassAnnouncerMenu();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("\nWRONG OPTION!\n");
            }

        } while (menuOption != 4);
    }

    /**
     * Prints the main menu
     */
    private static void displayMainMenu() {
        System.out.println("What would you like to do?\n" +
                "(1) Search Catalog\n" +
                "(2) Modify/View your custom schedules\n" +
                "(3) View Full Announcer\n" +
                "(4) Quit\n" +
                "Pick an option (1-4) and press ENTER.");
    }

    /**
     * Responsible for the menu for adding a class from the search catalog or full announcer
     * @throws SQLException
     */
    private static void getAddClassAnnouncerMenu() throws SQLException {
        Announcer announcer = new Announcer();
        int addClassOption;

        do {
            displayAddClassMenu();
            addClassOption = ScannerUtils.getInt();
            switch(addClassOption) {
                case 1:
                    Course course = announcer.getCourse();
                    CustomScheduleManager customScheduleManager = new CustomScheduleManager();
                    customScheduleManager.saveCourse(course);
                    System.out.println();
                    break;
                case 2:
                    System.out.println();
                    break;
                default:
                    System.out.println("WRONG OPTION!");
            }
        } while (addClassOption < 1 || addClassOption > 2);

    }

    /**
     * Prints the menu asking about adding classes to a schedule
     */
    private static void displayAddClassMenu() {
        System.out.println("\nWould you like to add any of the classes to one of your schedules?\n" +
                "(1) Yes\n" +
                "(2) No");
    }

    /**
     * The menu for the custom schedules
     * @throws SQLException
     */
    private static void getCustomScheduleMenu() throws SQLException {
        int menuOption;
        String scheduleName;
        Announcer announcer = new Announcer();
        CustomScheduleManager customScheduleManager = new CustomScheduleManager();

        do {
            displayCustomScheduleMenu();

            menuOption = ScannerUtils.getInt();

            switch (menuOption) {
                case 1:
                    if (announcer.runSearchCatalog() != 0) {
                        Course course = announcer.getCourse();
                        customScheduleManager.saveCourse(course);
                    }
                    break;
                case 2:
                    scheduleName = customScheduleManager.getScheduleName("select");
                    if (!scheduleName.equals("")) {
                        customScheduleManager.deleteCourse(scheduleName);
                    }
                    break;
                case 3:
                    customScheduleManager.createSchedule();
                    break;
                case 4:
                    scheduleName = customScheduleManager.getScheduleName("delete");
                    if (!scheduleName.equals("")) {
                        customScheduleManager.deleteSchedule(scheduleName);
                    }
                    break;
                case 5:
                    scheduleName = customScheduleManager.getScheduleName("view");
                    if (!scheduleName.equals("")) {
                        customScheduleManager.viewSchedule(scheduleName);
                    }
                    break;
                case 6:
                    scheduleName = customScheduleManager.getScheduleName("rename");
                    if (!scheduleName.equals("")) {
                        customScheduleManager.renameSchedule(scheduleName);
                    }
                    break;
                case 7:
                    scheduleName = customScheduleManager.getScheduleName("duplicate");
                    if (!scheduleName.equals("")) {
                        customScheduleManager.duplicateSchedule(scheduleName);
                    }
                    break;
                case 8:
                    System.out.println();
                    break;
                default:
                    System.out.println("\nWRONG OPTION!");
            }

        } while (menuOption != 8);
    }

    /**
     * Display the menu for custom schedule management
     */
    private static void displayCustomScheduleMenu() {
        System.out.println("\nWhat would you like to do?\n" +
                "(1) Add a course to a schedule\n" +
                "(2) Delete a course from a schedule\n" +
                "(3) Create a new schedule\n" +
                "(4) Delete a schedule\n" +
                "(5) View one of your schedules\n" +
                "(6) Rename a schedule\n" +
                "(7) Duplicate one of your schedules\n" +
                "(8) Quit");
    }
}
