package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities for converting resultSets to a List or a Course object and for printing out course lists
 */
final class DAOUtils {

    private final static List<String> columnTitles = new ArrayList<>(Arrays.asList("SubjectID",
            "CourseID", "Course Title", "ClassID", "Seats", "Code", "Block", "Room", "Teacher"));

    /**
     * To prevent instantiation
     */
    private DAOUtils() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Converts a resultSet to a List
     * @param resultSet The resultSet to be converted
     * @return The List containing the data from the resultSet
     * @throws SQLException
     */
    public static List<Course> resultSetToList(ResultSet resultSet) throws SQLException {
        List<Course> courseList = new ArrayList<>();

        while (resultSet.next()) {
            courseList.add(new Course(resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getInt(4),
                    resultSet.getInt(5),
                    (resultSet.getString(6)).charAt(0),
                    resultSet.getInt(7),
                    resultSet.getString(8),
                    resultSet.getString(9)));

        }

        return courseList;
    }

    /**
     * Converts a resultSet with one row to a Course object
     * @param resultSet The resultSet to be converted
     * @return A Course object containing the data from the resultSet
     * @throws SQLException
     */
    public static Course resultSetToCourse(ResultSet resultSet) throws SQLException {
        int i = 0;

        while (resultSet.next()) {
            i++;
        }

        resultSet.first();

        if (i == 1) {
            return new Course(resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getInt(4),
                    resultSet.getInt(5),
                    (resultSet.getString(6)).charAt(0),
                    resultSet.getInt(7),
                    resultSet.getString(8),
                    resultSet.getString(9));
        } else {
            return new Course();
        }
    }

    /**
     * Prints the course list
     * @param courseList The course list to be printed
     */
    public static void printCourseList(List<Course> courseList) {
        int courseTitleFormatWidth;
        int roomFormatWidth;

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
    private static int getCourseTitleFormatWidth(List<Course> courseList) {
        int courseTitleFormatWidth = columnTitles.get(2).length() + 1; //Store the default width of the course title

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
    private static int getRoomFormatWidth(List<Course> courseList) {
        int roomFormatWidth = columnTitles.get(7).length() + 1; //Store the default width of the course title

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
    private static void printColumnTitles(int courseTitleFormatWidth, int roomFormatWidth) {
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
    private static void printCourses(List<Course> courseList, int courseTitleFormatWidth,
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
     * Gets the number of courses from the course list
     * @param courseList The course list to be counted
     * @return An int holding the number of courses from the course list
     */
    public static int getCourseCount(List<Course> courseList) {
        return courseList.size();
    }
}
