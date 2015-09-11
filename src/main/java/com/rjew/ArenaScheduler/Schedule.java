package com.rjew.ArenaScheduler;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Schedule implements Serializable, Comparable<Schedule> {
    private final List<String> columnTitles = new ArrayList<>(Arrays.asList("SubjectID",
            "CourseID", "Course Title", "ClassID", "Seats", "Code", "Block", "Room", "Teacher"));

    private List<Course> courseList;
    private String name;//Must be unique
    private Integer rank;//Must be unique

    //Constructors

    public Schedule() {
        courseList = new ArrayList<>();
        name = null;
        rank = null;
    }

    public Schedule(String name) {
        courseList = new ArrayList<>();
        this.name = name;
        rank = null;
    }

    public Schedule(List<Course> courseList, String name) {
        this.courseList = courseList;
        this.name = name;
        rank = null;
    }

    public Schedule(List<Course> courseList, String name, Integer rank) {
        this.courseList = courseList;
        this.name = name;
        this.rank = rank;
    }

    public Schedule(Schedule schedule, String name) {
        courseList = new ArrayList<>();

        for (int i = 0; i < schedule.courseList.size(); i++) {
            this.courseList.add(schedule.courseList.get(i));
        }

        this.name = name;

        rank = null;
    }

    public Schedule(Schedule schedule, String name, Integer rank) {
        courseList = new ArrayList<>();

        for (int i = 0; i < schedule.courseList.size(); i++) {
            this.courseList.add(schedule.courseList.get(i));
        }

        this.name = name;

        this.rank = rank;
    }

    //Default compare Schedules by rank
    @Override
    public int compareTo(Schedule schedule) {
        if (this.rank < schedule.rank) {
            return -1;
        } else if (this.rank > schedule.rank) {
            return 1;
        } else {
            return 0;
        }
    }

    //Getters & Setters

    public List<Course> getCourseList() {
        return courseList;
    }

    public String getName() {
        return name;
    }

    public Integer getRank() {
        return rank;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    /**
     * Returns number of courses in Schedule
     * @return An int holding the number of courses in the schedule
     */
    public int getNumCourses() {
        return courseList.size();
    }

    /**
     * Adds a Course object to the schedule
     * @param course The course to be added to the schedule
     * @return A boolean indicating whether the course was successfully added
     * true=added successfully, false=could not be added
     */
    public boolean addCourse(Course course) {
        boolean courseLimitReached;
        boolean sameBlock;

        courseLimitReached = checkCourseLimit();

        if (course.getClassID() != 0) {

            sameBlock = checkSameBlock(course);

            if (!courseLimitReached && !sameBlock) {
                courseList.add(course);
                return true;
            } else {
                System.out.print("\nCannot add course. ");

                if (courseLimitReached) {
                    System.out.println("Course limit reached.");
                } else {
                    System.out.println("A course with the same block already exists in the schedule.");
                }

                return false;
            }
        } else {
            System.out.println("\nInvalid class id.");

            return false;
        }
    }

    /**
     * Checks if the course limit is reached
     * @return A boolean indicating whether the course limit is reached
     * true=course limit reached, false=course limit not reached
     */
    private boolean checkCourseLimit() {
        final int COURSE_LIMIT = 7;
        final int LAST_BLOCK = 8;

        boolean courseLimitReached = false;
        int numCourses = 0;

        for (Course course : courseList) {
            if (course.getBlock() < LAST_BLOCK) {
                numCourses++;
            }
        }

        if (numCourses >= COURSE_LIMIT) {
            courseLimitReached = true;
        }

        return courseLimitReached;
    }

    /**
     * Checks if the block of the course being added already exists in the schedule
     * @param course The Course to be added to the schedule
     * @return A boolean indicating whether the course with the same block already exists
     * true=course with same block exists, false=course with same block does not exist
     */
    private boolean checkSameBlock(Course course) {
        boolean sameBlock = false;

        for (Course aCourseList : courseList) {
            if (aCourseList.getBlock().equals(course.getBlock())) {
                sameBlock = true;
            }
        }

        return sameBlock;
    }

    /**
     * Deletes a course from the schedule
     * @param classID The class ID of the course to be deleted
     * @return A boolean indicating whether the course was successfully deleted
     * true=course with classID could be deleted, false=course with classID does not exist in the schedule
     */
    public boolean deleteCourse(int classID) {
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getClassID() == classID) {
                courseList.remove(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Deletes a course from the schedule
     * @param course The course to be deleted
     * @return A boolean indicating whether the course was successfully deleted
     * true=course could be deleted, false=course does not exist in the schedule
     */
    public boolean deleteCourse(Course course) {
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).equals(course)) {
                courseList.remove(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Prints the schedule
     */
    public void print() {
        int courseTitleFormatWidth;
        int roomFormatWidth;

        Collections.sort(courseList);

        courseTitleFormatWidth = getCourseTitleFormatWidth(courseList);

        roomFormatWidth = getRoomFormatWidth(courseList);

        System.out.println();

        printColumnTitles(courseTitleFormatWidth, roomFormatWidth);

        System.out.println();

        printCourses(courseList, courseTitleFormatWidth, roomFormatWidth);
    }

    /**
     * Gets the course title column width for displaying course title column
     * @param courseList The course list to be printed
     * @return An int holding the course title column width
     */
    private int getCourseTitleFormatWidth(List<Course> courseList) {
        int courseTitleFormatWidth = columnTitles.get(3).length() + 1; //Store the default width of the course title

            /* If one of the course titles has a longer name, store it for the width */
        for (Course course : courseList) {
            if (course.getCourseTitle().length() + 1 > courseTitleFormatWidth) {
                courseTitleFormatWidth = course.getCourseTitle().length() + 1;
            }
        }

        return courseTitleFormatWidth;
    }

    /**
     * Gets the room column width for displaying room column
     * @param courseList The course list to be printed
     * @return An int holding the room column width
     */
    private int getRoomFormatWidth(List<Course> courseList) {
        int roomFormatWidth = columnTitles.get(8).length() + 1; //Store the default width of the course title

            /* If one of the course titles has a longer name, store it for the width */
        for (Course course : courseList) {
            if (course.getRoom().length() + 1 > roomFormatWidth) {
                roomFormatWidth = course.getRoom().length() + 1;
            }
        }

        return roomFormatWidth;
    }

    /**
     * Prints the column titles
     * @param courseTitleFormatWidth An int holding the course title column width
     * @param roomFormatWidth An int holding the room title column width
     */
    private void printColumnTitles(int courseTitleFormatWidth, int roomFormatWidth) {
        /* Print out the column titles */
        for (int i = 0; i < columnTitles.size(); i++) {
            switch (i) {
                case 0:
                    System.out.printf("%-11s", columnTitles.get(i) + " ");
                    break;
                case 1:
                    System.out.printf("%-10s", columnTitles.get(i) + " ");
                    break;
                case 2:
                    System.out.printf("%-" + courseTitleFormatWidth + "s", columnTitles.get(i) + " ");
                    break;
                case 3:
                    System.out.printf("%-9s", columnTitles.get(i) + " ");
                    break;
                case 4:
                    System.out.printf("%-6s", columnTitles.get(i) + " ");
                    break;
                case 5:
                    System.out.printf("%-5s", columnTitles.get(i) + " ");
                    break;
                case 6:
                    System.out.printf("%-6s", columnTitles.get(i) + " ");
                    break;
                case 7:
                    System.out.printf("%-" + roomFormatWidth + "s", columnTitles.get(i) + " ");
                    break;
                case 8:
                    System.out.print(columnTitles.get(i));
                    break;
            }
        }
    }

    /**
     * Prints course list data
     * @param courseList The course list to be printed
     * @param courseTitleFormatWidth An int holding the course title column width
     * @param roomFormatWidth An int holding the room title column width
     */
    private void printCourses(List<Course> courseList, int courseTitleFormatWidth,
                                     int roomFormatWidth) {
                /* Print out the database results */
        for (Course course : courseList) {
            for (int j = 1; j <= columnTitles.size(); j++) {
                switch (j) {
                    case 1:
                        System.out.printf("%-11s", course.getSubjectID() + " ");
                        break;
                    case 2:
                        System.out.printf("%-10s", course.getCourseID() + " ");
                        break;
                    case 3:
                        System.out.printf("%-" + courseTitleFormatWidth + "s", course.getCourseTitle() + " ");
                        break;
                    case 4:
                        System.out.printf("%-9s", course.getClassID() + " ");
                        break;
                    case 5:
                        System.out.printf("%-6s", course.getSeats() + " ");
                        break;
                    case 6:
                        System.out.printf("%-5s", course.getCode() + " ");
                        break;
                    case 7:
                        System.out.printf("%-6s", course.getBlock() + " ");
                        break;
                    case 8:
                        System.out.printf("%-" + roomFormatWidth + "s", course.getRoom() + " ");
                        break;
                    case 9:
                        System.out.print(course.getTeacher());
                        break;
                }
            }
            System.out.println();
        }
    }

    /**
     * The name is unique for each Schedule. So this should compare Schedule by name only.
     */
    @Override
    public boolean equals(Object other) {
        return (other instanceof Schedule) && (name != null)
                ? name.equals(((Schedule) other).getName())
                : (other == this);
    }

    /**
     * The name is unique for each Schedule. So a Schedule with same name should return same hashcode.
     */
    @Override
    public int hashCode() {
        return (name != null)
                ? (this.getClass().hashCode() + name.hashCode())
                : super.hashCode();
    }

    /**
     * Returns the String representation of this Schedule.
     */
    @Override
    public String toString() {
        return String.format("Schedule\n" + "name=%s\n" + "rank=%d\n" + courseList,
                name, rank);
    }
}