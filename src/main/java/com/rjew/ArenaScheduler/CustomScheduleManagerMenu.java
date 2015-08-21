package com.rjew.ArenaScheduler;

import java.util.ArrayList;
import java.util.Scanner;

public class CustomScheduleManagerMenu {
    //todo add rename and copy schedule functions
    //todo ranking
    public static void accessCustomSchedules(Scanner keyboard) {
        int menuOption;

        do {
            displayCustomSchedulesMenu();

            menuOption = getCustomSchedulesMenuOption(keyboard);

        } while (menuOption != 6);
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
        int scheduleOption;
        ArrayList<String> tableNamesArrayList = new ArrayList<String>();

        try {
            menuOption = keyboard.nextInt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        switch (menuOption) {
            case 1://todo fix this case so the user doesn't have to select a schedule again
                try {
                    SearchCatalog.displaySearchCatalog(keyboard);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case 2:
                scheduleOption = CustomScheduleManagerSelection.displayScheduleOptions(keyboard, tableNamesArrayList, "select");
                if (scheduleOption != 0) {
                    CustomScheduleManagerDeleteClass.deleteClass(keyboard, tableNamesArrayList.get(scheduleOption - 1));
                }
                break;
            case 3:
                try {
                    CustomScheduleManagerCreateSchedule.createSchedule(keyboard);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case 4:
                scheduleOption = CustomScheduleManagerSelection.displayScheduleOptions(keyboard, tableNamesArrayList, "delete");
                if (scheduleOption != 0) {
                    CustomScheduleManagerDeleteSchedule.deleteSchedule(tableNamesArrayList.get(scheduleOption - 1));
                }
                break;
            case 5:
                scheduleOption = CustomScheduleManagerSelection.displayScheduleOptions(keyboard, tableNamesArrayList, "view");
                if (scheduleOption != 0) {
                    CustomScheduleManagerViewSchedule.viewSchedule(tableNamesArrayList.get(scheduleOption - 1));
                }
                break;
            case 6:
                break;
            default:
                System.out.println("WRONG OPTION!");
        }

        return menuOption;
    }
}
