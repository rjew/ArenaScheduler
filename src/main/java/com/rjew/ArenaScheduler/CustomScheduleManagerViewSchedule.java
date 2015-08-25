package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.*;

public class CustomScheduleManagerViewSchedule {

    final static Logger logger = Logger.getLogger(CustomScheduleManagerViewSchedule.class);

    public static boolean viewSchedule(String tableName) {
        final String CUSTOM_SCHEDULE_DB_URL = "jdbc:derby:Custom_Schedules;create=true"; //For db Connection

        try (Connection customScheduleConn = DriverManager.getConnection(CUSTOM_SCHEDULE_DB_URL);
             Statement customScheduleStatement =
                     customScheduleConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            String sqlStatement = "SELECT subject_id, course_id, " +
                    "course_title, class_id, " +
                    "seats, code, block, room, teacher " +
                    "FROM \"" + tableName + "\"" +
                    "ORDER BY block";

            try (ResultSet customScheduleResultSet = customScheduleStatement.executeQuery(sqlStatement)) {
                int numRows = ExecuteSQL.getResultCount(customScheduleResultSet);

                if (numRows != 0) {
                    ResultSetMetaData customScheduleRSMeta = customScheduleResultSet.getMetaData();
                    printSchedule(customScheduleResultSet, customScheduleRSMeta);

                    return true;
                } else {
                    System.out.println(tableName + " has no classes!");

                    return false;
                }
            }

        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }

        return false;
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
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
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
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
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
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
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
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
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
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
    }
}
