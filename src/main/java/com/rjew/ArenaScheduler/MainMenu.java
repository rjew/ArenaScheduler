package com.rjew.ArenaScheduler;

import java.util.Scanner;

public class MainMenu {
    public static void displayArenaSchedulerMenu() {
        int option;

        Scanner keyboard = new Scanner(System.in);

        /* Print the Menu */
        do {
            System.out.println("Welcome to the Arena Scheduler Program!\n"
                    + "What would you like to do?\n"
                    + "(1) Search Catalog\n"
                    + "(2) Modify/View your custom schedules\n"
                    + "(3) Quit\n"
                    + "Pick an option (1-3) and press ENTER.");//todo View full announcer - allow add class to schedule

            option = getArenaSchedulerOption(keyboard);

        } while (option != 3);

        keyboard.close();
    }

    public static int getArenaSchedulerOption(Scanner keyboard) {
        int option = 0;

        try {
            option = keyboard.nextInt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        switch (option) {
            case 1:
                SearchCatalog.displaySearchCatalog(keyboard);
                break;
            case 2:
                CustomScheduleManagerMenu.accessCustomSchedules(keyboard);
                break;
            case 3:
                break;
            default:
                System.out.println("WRONG OPTION!");
        }

        return option;
    }
}
