package com.rjew.ArenaScheduler;

import java.io.*;
import java.sql.SQLException;

/**
 * Responsible for displaying the menu and getting user input regarding what the user wants to do
 */
final class Menu {

    /**
     * To prevent instantiation
     */
    private Menu() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static void getArenaSchedulerMenu() throws SQLException, IOException, ClassNotFoundException {
        CustomScheduleManager customScheduleManager;

        try (FileInputStream fileInputStream = new FileInputStream("CustomScheduleManager.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)
        ) {
            customScheduleManager = (CustomScheduleManager) objectInputStream.readObject();
        } catch(FileNotFoundException ex) {
            customScheduleManager = new CustomScheduleManager();//CustomScheduleManager does not exist yet
        }

        System.out.println("Welcome to the Arena Scheduler Program!\n");

        getMainMenu(customScheduleManager);

        try (FileOutputStream fileOutputStream = new FileOutputStream("CustomScheduleManager.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ) {
            objectOutputStream.writeObject(customScheduleManager);
        }
    }

    private static void getMainMenu(CustomScheduleManager customScheduleManager) throws SQLException {
        int menuOption;
        Announcer announcer = new Announcer();

        /* Print the Menu */
        do {
            displayMainMenu();

            menuOption = ScannerUtils.getInt();

            switch (menuOption) {
                case 1:
                    if (announcer.runSearchCatalog() != 0) {
                        getAddClassAnnouncerMenu(customScheduleManager);
                    }
                    break;
                case 2:
                    getCustomScheduleMenu(customScheduleManager);
                    break;
                case 3:
                    announcer.printFullAnnouncer();
                    getAddClassAnnouncerMenu(customScheduleManager);
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
    private static void getAddClassAnnouncerMenu(CustomScheduleManager customScheduleManager) throws SQLException {
        Announcer announcer = new Announcer();
        int addClassOption;

        do {
            displayAddClassMenu();
            addClassOption = ScannerUtils.getInt();
            switch(addClassOption) {
                case 1:
                    Course course = announcer.getCourse();
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

    private static void getCustomScheduleMenu(CustomScheduleManager customScheduleManager) throws SQLException {
        int menuOption;
        int scheduleListIndex;
        Announcer announcer = new Announcer();

        do {
            displayCustomScheduleMenu();

            menuOption = ScannerUtils.getInt();

            switch (menuOption) {
                case 1:
                    if (announcer.runSearchCatalog() != 0) {//Result of query does not have 0 results
                        Course course = announcer.getCourse();
                        customScheduleManager.saveCourse(course);
                    }
                    break;
                case 2:
                    scheduleListIndex = customScheduleManager.getScheduleName("select");
                    if (scheduleListIndex != -1) {
                        customScheduleManager.deleteCourse(scheduleListIndex);
                    }
                    break;
                case 3:
                    customScheduleManager.createSchedule();
                    break;
                case 4:
                    scheduleListIndex = customScheduleManager.getScheduleName("delete");
                    if (scheduleListIndex != -1) {
                        customScheduleManager.deleteSchedule(scheduleListIndex);
                    }
                    break;
                case 5:
                    scheduleListIndex = customScheduleManager.getScheduleName("view");
                    if (scheduleListIndex != -1) {
                        customScheduleManager.viewSchedule(scheduleListIndex);
                    }
                    break;
                case 6:
                    scheduleListIndex = customScheduleManager.getScheduleName("rename");
                    if (scheduleListIndex != -1) {
                        customScheduleManager.renameSchedule(scheduleListIndex);
                    }
                    break;
                case 7:
                    scheduleListIndex = customScheduleManager.getScheduleName("duplicate");
                    if (scheduleListIndex != -1) {
                        customScheduleManager.duplicateSchedule(scheduleListIndex);
                    }
                    break;
                case 8:
                    getChangeRankingsMenu(customScheduleManager);
                    break;
                case 9:
                    System.out.println();
                    break;
                default:
                    System.out.println("\nWRONG OPTION!");
            }

        } while (menuOption != 9);
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
                "(8) View schedule rankings\n" +
                "(9) Quit");
    }

    private static void getChangeRankingsMenu(CustomScheduleManager customScheduleManager) {
        int changeRankingsOption;

        boolean schedulesExist = customScheduleManager.displayRankings();

        if (schedulesExist) {
            do {
                displayChangeRankingsMenu();
                changeRankingsOption = ScannerUtils.getInt();
                switch (changeRankingsOption) {
                    case 1:
                        int scheduleListIndex = customScheduleManager.getScheduleName("change");
                        customScheduleManager.changeRankings(scheduleListIndex);
                        break;
                    case 2:
                        System.out.println();
                        break;
                    default:
                        System.out.println("WRONG OPTION!");
                }
            } while (changeRankingsOption < 1 || changeRankingsOption > 2);
        }
    }

    private static void displayChangeRankingsMenu() {
        System.out.println("\nWould you like to change any of the schedule rankings?\n" +
                "(1) Yes\n" +
                "(2) No");
    }
}
