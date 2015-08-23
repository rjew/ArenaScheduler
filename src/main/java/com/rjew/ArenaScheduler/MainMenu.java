package com.rjew.ArenaScheduler;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MainMenu {
    public static void displayArenaSchedulerMenu() {
        int option;

        Scanner keyboard = new Scanner(System.in);

        /* Print the Menu */
        do {
            displayMainMenu();

            option = getArenaSchedulerOption(keyboard);

        } while (option != 4);

        keyboard.close();
    }

    public static void displayMainMenu() {
        System.out.println("Welcome to the Arena Scheduler Program!\n" +
                "What would you like to do?\n" +
                "(1) Search Catalog\n" +
                "(2) Modify/View your custom schedules\n" +
                "(3) View Full Announcer\n" +
                "(4) Quit\n" +
                "Pick an option (1-4) and press ENTER.");
    }

    public static int getArenaSchedulerOption(Scanner keyboard) {
        int option = 0;

        try {
            option = keyboard.nextInt();
        } catch (InputMismatchException ex) {
            //ignore exception, prompt user again for input if input is incorrect
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        keyboard.nextLine();//clear the buffer

        switch (option) {
            case 1:
                SearchCatalog.displaySearchCatalog(keyboard);
                break;
            case 2:
                CustomScheduleManagerMenu.accessCustomSchedules(keyboard);
                break;
            case 3:
                FullAnnouncer.viewFullAnnouncer(keyboard);
                break;
            case 4:
                break;
            default:
                System.out.println("WRONG OPTION!");
        }

        return option;
    }
}
