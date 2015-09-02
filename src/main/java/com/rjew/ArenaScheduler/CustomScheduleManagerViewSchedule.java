package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.*;

final class CustomScheduleManagerViewSchedule {

    private final static Logger logger = Logger.getLogger(CustomScheduleManagerViewSchedule.class);

    /**
     * To prevent instantiation
     */
    private CustomScheduleManagerViewSchedule() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Responsible for showing the schedule the user selected
     * @param tableName The schedule name the user want to view
     * @return A boolean indicating whether or not the schedule was successfully printed,
     * true=schedule printed successfully, false=schedule did not print successfully
     */
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
                    System.out.println("\n" + tableName + " has no classes!");

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

    /**
     * Prints the schedule into a table with column names and data
     * @param resultSet ResultSet holding the data for the classes to be printed
     * @param RSMetaData ResultSetMetaData holding the data for the column titles and column count
     */
    public static void printSchedule(ResultSet resultSet, ResultSetMetaData RSMetaData) {
        try {
            int courseTitleFormatWidth;
            int roomFormatWidth;

            courseTitleFormatWidth = getCourseTitleFormatWidth(resultSet, RSMetaData);

            roomFormatWidth = getRoomFormatWidth(resultSet, RSMetaData);

            System.out.println();

            printColumnTitles(RSMetaData, courseTitleFormatWidth, roomFormatWidth);

            System.out.println();

            printDBResults(resultSet, RSMetaData, courseTitleFormatWidth, roomFormatWidth);

        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }

    }

    /**
     * Gets the width for the Course Title column
     * @param resultSet ResultSet holding the data for the classes to be printed
     * @param RSMetaData ResultSetMetaData holding the data for the column titles and column count
     * @return An int holding the Course Title column width
     */
    private static int getCourseTitleFormatWidth(ResultSet resultSet, ResultSetMetaData RSMetaData) {
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

    /**
     * Gets the width for the Room column
     * @param resultSet Result Set holding the data for the classes to be printed
     * @param RSMetaData ResultSetMetaData holding the data for the column titles and column count
     * @return An int holding the Room column width
     */
    private static int getRoomFormatWidth(ResultSet resultSet, ResultSetMetaData RSMetaData) {
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

    /**
     * Prints the column headings
     * @param RSMetaData ResultSetMetaData holding the data for the column titles and column count
     * @param courseTitleFormatWidth An int holding the width of the Course Title column
     * @param roomFormatWidth An int holding the width of the Room column
     */
    private static void printColumnTitles(ResultSetMetaData RSMetaData, int courseTitleFormatWidth,
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

    /**
     * Prints the schedule of classes
     * @param resultSet ResultSet holding the data for the classes to be printed
     * @param RSMetaData ResultSetMetaData holding the data for the column titles and column count
     * @param courseTitleFormatWidth An int holding the width of the Course Title column
     * @param roomFormatWidth An int holding the width of the Room column
     */
    private static void printDBResults(ResultSet resultSet, ResultSetMetaData RSMetaData,
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
