package com.rjew.ArenaScheduler;

import java.sql.*;

public class CustomScheduleManagerViewSchedule {
    //todo handle if schedule is empty, not classes in schedule
    public static void viewSchedule(String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Custom_Schedules"; //For db Connection

        try {

            Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);

            Statement customScheduleStatement = customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            String sqlStatement = "SELECT subject_id, course_id, " +
                    "course_title, class_id, " +
                    "seats, code, block, room, teacher " +
                    "FROM \"" + tableName + "\"" +
                    "ORDER BY block";
            ResultSet customScheduleResultSet = customScheduleStatement.executeQuery(sqlStatement);
            ResultSetMetaData customScheduleRSMeta = customScheduleResultSet.getMetaData();
            printSchedule(customScheduleResultSet, customScheduleRSMeta);

            customScheduleConn.close();
            customScheduleResultSet.close();
            customScheduleStatement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void printSchedule(ResultSet resultSet, ResultSetMetaData RSMetaData) {
        try {
            int courseTitleFormatWidth;
            int roomFormatWidth;

            courseTitleFormatWidth = getCourseTitleFormatWidth(resultSet, RSMetaData);

            roomFormatWidth = getRoomFormatWidth(resultSet, RSMetaData);

            printColumnTitles(RSMetaData, courseTitleFormatWidth, roomFormatWidth);

            System.out.println();

            printDBResults(resultSet, RSMetaData, courseTitleFormatWidth, roomFormatWidth);

        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

    }

    public static int getCourseTitleFormatWidth(ResultSet resultSet, ResultSetMetaData RSMetaData) {
        try {
            int courseTitleFormatWidth = RSMetaData.getColumnName(3).length() + 1; //Store the default width of the course title

            /* If one of the course titles has a longer name, store it for the width */
            while (resultSet.next()) {
                if (resultSet.getString(3).length() + 1 > courseTitleFormatWidth) {
                    courseTitleFormatWidth = resultSet.getString(3).length() + 1;
                }
            }

            /* Roll back the resultSet */
            resultSet.beforeFirst();

            return courseTitleFormatWidth;
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public static int getRoomFormatWidth(ResultSet resultSet, ResultSetMetaData RSMetaData) {
        try {
            int roomFormatWidth = RSMetaData.getColumnName(8).length() + 1;//Store the default width of the room

            /* If one of the rooms has a longer name, store it for the width */
            while (resultSet.next()) {
                if (resultSet.getString(8).length() + 1 > roomFormatWidth) {
                    roomFormatWidth = resultSet.getString(8).length() + 1;
                }
            }

            /* Roll back the resultSet */
            resultSet.first();

            return roomFormatWidth;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public static void printColumnTitles(ResultSetMetaData RSMetaData, int courseTitleFormatWidth,
                                         int roomFormatWidth) {
        /* Print out the column titles */
        try {
            for (int i = 1; i <= RSMetaData.getColumnCount(); i++) {
                switch (i) {
                    case 1:
                        System.out.printf("%-11s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 2:
                        System.out.printf("%-10s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 3:
                        System.out.printf("%-" + courseTitleFormatWidth + "s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 4:
                        System.out.printf("%-9s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 5:
                        System.out.printf("%-6s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 6:
                        System.out.printf("%-5s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 7:
                        System.out.printf("%-6s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 8:
                        System.out.printf("%-" + roomFormatWidth + "s", RSMetaData.getColumnName(i) + " ");
                        break;
                    case 9:
                        System.out.print(RSMetaData.getColumnName(i));
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void printDBResults(ResultSet resultSet, ResultSetMetaData RSMetaData,
                                      int courseTitleFormatWidth, int roomFormatWidth) {
        /* Print out the database results */
        try {
            do {
                for (int i = 1; i <= RSMetaData.getColumnCount(); i++) {
                    switch (i) {
                        case 1:
                            System.out.printf("%-11s", resultSet.getString(i) + " ");
                            break;
                        case 2:
                            System.out.printf("%-10s", resultSet.getString(i) + " ");
                            break;
                        case 3:
                            System.out.printf("%-" + courseTitleFormatWidth + "s", resultSet.getString(i) + " ");
                            break;
                        case 4:
                            System.out.printf("%-9s", resultSet.getString(i) + " ");
                            break;
                        case 5:
                            System.out.printf("%-6s", resultSet.getString(i) + " ");
                            break;
                        case 6:
                            System.out.printf("%-5s", resultSet.getString(i) + " ");
                            break;
                        case 7:
                            System.out.printf("%-6s", resultSet.getString(i) + " ");
                            break;
                        case 8:
                            System.out.printf("%-" + roomFormatWidth + "s", resultSet.getString(i) + " ");
                            break;
                        case 9:
                            System.out.print(resultSet.getString(i));
                            break;
                    }
                }
                System.out.println();
            } while (resultSet.next());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}