package com.rjew.ArenaScheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for handling the custom schedules that the user creates
 */
class CustomScheduleManager implements Serializable{
    private static final long serialVersionUID = 2007944171822883301L;

    private List<Schedule> scheduleList;

    public CustomScheduleManager() {
        scheduleList = new ArrayList<>();
    }

    /**
     * Displays the available custom schedules and returns the index of the user's schedule choice
     * @param displayOption For the output corresponding to the custom schedule modification option
     * @return An int holding the index of the user's selected schedule
     */
    public int getScheduleName(String displayOption) {
        int scheduleOption;

        if (scheduleList.size() != 0) {

            displaySchedules(displayOption);

            scheduleOption = ScannerUtils.getInt();

            while (scheduleOption <= 0 || scheduleOption > scheduleList.size()) {
                System.out.println("\nWRONG OPTION!");
                displaySchedules(displayOption);

                scheduleOption = ScannerUtils.getInt();
            }

            return scheduleOption - 1;//Index for the schedule that the user chose in scheduleList

        } else {
            System.out.println("\nNo schedules available.");

            return -1;
        }
    }

    /**
     * Displays the custom schedules that the user can choose from
     * @param displayOption A String that changes the output depending of the user's custom schedule manager choice
     */
    private void displaySchedules(String displayOption) {
        System.out.println("\nWhich schedule would you like to " + displayOption + "?");

        for (int i = 0; i < scheduleList.size(); i++) {
            System.out.print("(" + (i + 1) + ") ");
            System.out.println(scheduleList.get(i).getName());
        }
    }

    /**
     * Responsible for saving a class that the user selects from the search catalog
     * @param course A Course object that hold the course that the user wants to add
     */
    public void saveCourse(Course course) {
        int scheduleOption;
        boolean addClassSuccessful;

        if (scheduleList.size() != 0) {

            do {
                displaySchedules("add the class to");
                System.out.println("(" + (scheduleList.size() + 1) + ") Create new schedule");

                scheduleOption = ScannerUtils.getInt();

                if (scheduleOption < 1 || scheduleOption > scheduleList.size() + 1) {
                    System.out.println("WRONG OPTION!");
                }
            } while (scheduleOption < 1 || scheduleOption > scheduleList.size() + 1);

            if (scheduleOption != scheduleList.size() + 1) {
                addClassSuccessful = scheduleList.get(scheduleOption - 1).addCourse(course);
                if (addClassSuccessful) {
                    System.out.println("\nClass " + course.getClassID() + " has been added to " +
                            scheduleList.get(scheduleOption - 1).getName() + ".");
                }
                viewSchedule(scheduleOption - 1);//Index of schedule in scheduleList
            } else {
                System.out.println("\nCreating new schedule");
                addCourse(course);
            }
        } else {
            System.out.println("\nNo schedule found... Creating new schedule");
            addCourse(course);
        }
    }

    /**
     * Creates a new schedule and adds the specified course to the schedule
     * @param course A Course object that holds the course that the user wants to add
     */
    private void addCourse(Course course) {
        boolean addCourseSuccessful;
        String scheduleName;

        scheduleName = createSchedule();

        addCourseSuccessful = scheduleList.get(scheduleList.size() - 1).addCourse(course); //Last index is new schedule
        if (addCourseSuccessful) {
            System.out.println("Class " + course.getClassID() + " has been added to " + scheduleName + ".");
        }
        viewSchedule(scheduleList.size() - 1); //Index of new schedule
    }

    /**
     * Gets the class ID of the course that the user wants to delete and removes it from the specified schedule
     * @param scheduleListIndex The schedule index in which the course will be removed
     */
    public void deleteCourse(int scheduleListIndex) {
        int classID;
        boolean deleteClassSuccessful;
        boolean properSchedule;

        properSchedule = viewSchedule(scheduleListIndex);

        if (properSchedule) {
            do {
                System.out.println("Which class would you like to delete?\n" +
                        "Enter the Class ID of the course you would like to delete:");
                classID = ScannerUtils.getInt();

                if (classID < 1) {
                    System.out.println("\nWRONG OPTION!\n");
                }
            } while (classID < 1);

            deleteClassSuccessful = scheduleList.get(scheduleListIndex).deleteCourse(classID);

            if (deleteClassSuccessful) {
                System.out.println("\nClass " + classID + " deleted.");
                viewSchedule(scheduleListIndex);
            } else {
                System.out.println("\nClass " + classID + " does not exist in " +
                        scheduleList.get(scheduleListIndex).getName() + ".");
                deleteCourse(scheduleListIndex);
            }
        }
    }

    /**
     * Gets the new schedule name and creates the new schedule
     * @return A String holding the new schedule name
     */
    public String createSchedule() {
        String scheduleName;

        System.out.println("\nEnter the new schedule name:");
        scheduleName = ScannerUtils.getString();

        while(checkScheduleExists(scheduleName)) {
            System.out.println("\nSchedule with the same name already exists.\n\nEnter the new schedule name:");
            scheduleName = ScannerUtils.getString();
        }

        scheduleList.add(new Schedule(scheduleName));

        System.out.println("\n" + scheduleName + " created.");

        return scheduleName;
    }

    /**
     * Deletes the specified schedule
     * @param scheduleListIndex The index of the schedule to be deleted
     */
    public void deleteSchedule(int scheduleListIndex) {
        String scheduleName = scheduleList.get(scheduleListIndex).getName();

        scheduleList.remove(scheduleListIndex);

        System.out.println("\n" + scheduleName + " deleted.");
    }

    /**
     * Responsible for renaming the specified schedule
     * @param scheduleListIndex Holds the index of the schedule to be renamed
     */
    public void renameSchedule(int scheduleListIndex) {
        String oldScheduleName = scheduleList.get(scheduleListIndex).getName();
        String newScheduleName;

        System.out.println("\nEnter the new schedule name:");
        newScheduleName = ScannerUtils.getString();


        while(checkScheduleExists(newScheduleName)) {
            System.out.println("\nSchedule with the same name already exists.\n\nEnter the new schedule name:");
            newScheduleName = ScannerUtils.getString();
        }

        scheduleList.get(scheduleListIndex).setName(newScheduleName);

        System.out.println("\n" + oldScheduleName + " is now renamed to " + newScheduleName + ".");
    }

    /**
     *Responsible for making a copy of a schedule
     * @param scheduleListIndex The index of the schedule to be duplicated
     */
    public void duplicateSchedule(int scheduleListIndex) {
        String duplicateScheduleName;

        System.out.println("\nEnter the duplicate schedule name:");
        duplicateScheduleName = ScannerUtils.getString();

        while(checkScheduleExists(duplicateScheduleName)) {
            System.out.println("\nSchedule with the same name already exists.\n\nEnter the duplicate schedule name:");
            duplicateScheduleName = ScannerUtils.getString();
        }

        scheduleList.add(new Schedule(scheduleList.get(scheduleListIndex), duplicateScheduleName));

        System.out.println("\n" + scheduleList.get(scheduleListIndex).getName() +
                " has been copied to " + duplicateScheduleName + ".");

    }

    /**
     * Print out the specified schedule
     * @param scheduleListIndex The index of the schedule to be printed
     * @return A boolean value indicating whether the schedule was printed or not
     * true=schedule could be printed, false=schedule could not be printed, no classes in schedule
     */
    public boolean viewSchedule(int scheduleListIndex) {
        int numRows = scheduleList.get(scheduleListIndex).getNumCourses();

        if (numRows != 0) {
            scheduleList.get(scheduleListIndex).print();
            return true;
        } else {
            System.out.println("\n" + scheduleList.get(scheduleListIndex).getName() + " has no classes!");
            return false;
        }
    }

    /**
     * Displays the rankings of the schedules
     * @return A boolean indicating whether there are any available schedules
     * true=schedules are available, false=schedules are not available
     */
    public boolean displayRankings() {
        if (scheduleList.size() != 0) {
            List<Schedule> scheduleListWithRank = new ArrayList<>();
            List<Schedule> scheduleListWithoutRank = new ArrayList<>();

            System.out.println("\nSchedules with rankings:");

            for (Schedule schedule : scheduleList) {
                if (schedule.getRank() != null) {
                    scheduleListWithRank.add(schedule);
                } else {
                    scheduleListWithoutRank.add(schedule);
                }
            }

            if (scheduleListWithRank.size() != 0) {
                Collections.sort(scheduleListWithRank);

                for (Schedule schedule : scheduleListWithRank) {
                    System.out.println("Rank " + schedule.getRank() + " - " + schedule.getName());
                }

            } else {
                System.out.println("No schedule with rankings.");
            }

            System.out.println("\nSchedules without rankings:");

            if (scheduleListWithoutRank.size() != 0) {
                for (Schedule schedule : scheduleListWithoutRank) {
                    System.out.println(schedule.getName());
                }
            } else {
                System.out.println("All schedules have rankings.");
            }

            return true;
        } else {
            System.out.println("\nNo schedules available.");

            return false;
        }
    }

    /**
     * Responsible for changing the rank of the specified Schedule
     * @param scheduleListIndex The index of the schedule to be changed
     */
    public void changeRankings(int scheduleListIndex) {
        int newRank;
        boolean rankExists;

        do {

            do {
                System.out.println("\nEnter the rank that you like to change the schedule to:");
                newRank = ScannerUtils.getInt();

                if (newRank < 1) {
                    System.out.println("\nA schedule cannot have a rank less than 1.");
                }
            } while (newRank < 1);

            rankExists = false;

            for (Schedule schedule : scheduleList) {
                if (schedule.getRank() != null) {
                    if (schedule.getRank() == newRank) {
                        rankExists = true;
                    }
                }
            }

            if (rankExists) {
                System.out.println("\nA schedule with rank " + newRank + " already exists.");
            }
        } while (rankExists);

        scheduleList.get(scheduleListIndex).setRank(newRank);

        System.out.println("\n" + scheduleList.get(scheduleListIndex).getName() +
                " now has a rank of " + newRank + ".");
    }

    /**
     * Checks if the schedule with the name already exists
     * @param name The name to be checked
     * @return A boolean indicating whether the schedule name already exists
     * true=already exists, false=does not exist
     */
    private boolean checkScheduleExists(String name) {
        for (Schedule schedule : scheduleList) {
            if (schedule.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }
}
