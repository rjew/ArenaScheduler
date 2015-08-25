package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomScheduleManagerAddCourse {

    final static Logger logger = Logger.getLogger(CustomScheduleManagerAddCourse.class);

    public static boolean addCourse(int classID, String tableName,
                                    Statement announcerStatement, Statement customScheduleStatement) {
        boolean courseLimitReached;
        boolean sameBlock;

        courseLimitReached = checkCourseLimit(tableName, customScheduleStatement);

        String getCourseSQLString = "SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk AND " +
                "class_id = " + classID;

        try (ResultSet courseResultSet = announcerStatement.executeQuery(getCourseSQLString)) {

            int numRows = ExecuteSQL.getResultCount(courseResultSet);

            if (numRows != 0) {

                courseResultSet.next();
                int courseBlock = courseResultSet.getInt(7);

                sameBlock = checkSameBlock(tableName, customScheduleStatement, courseBlock);

                if (!courseLimitReached && !sameBlock) {
                    insertCourse(tableName, courseResultSet, customScheduleStatement);
                    return true;
                } else {
                    System.out.print("Cannot add course. ");

                    if (courseLimitReached) {
                        System.out.println("Course limit reached.");
                    } else {
                        System.out.println("A course with the same block already exists in the schedule.");
                    }

                    return false;
                }
            } else {
                System.out.println("Invalid class id.");

                return false;
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

    public static boolean checkCourseLimit(String tableName, Statement customScheduleStatement) {
        final int COURSE_LIMIT = 7;
        final int LAST_BLOCK = 8;

        boolean courseLimitReached = false;
        int numCourses;

        String courseLimitCheck = "SELECT COUNT(*) FROM \"" + tableName + "\" WHERE block <= " + LAST_BLOCK;

        try (ResultSet courseLimitCheckRS = customScheduleStatement.executeQuery(courseLimitCheck)) {
            courseLimitCheckRS.next();
            numCourses = courseLimitCheckRS.getInt(1);
            if (numCourses >= COURSE_LIMIT) {
                courseLimitReached = true;
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }

        return courseLimitReached;
    }

    public static boolean checkSameBlock(String tableName, Statement customScheduleStatement, int courseBlock) {
        boolean sameBlock = false;

        String sameBlockCheck = "SELECT block FROM \"" + tableName + "\"";

        try (ResultSet sameBlockCheckRS = customScheduleStatement.executeQuery(sameBlockCheck)) {
            while (sameBlockCheckRS.next()) {
                if (sameBlockCheckRS.getInt(1) == courseBlock) {
                    sameBlock = true;
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }

        return sameBlock;
    }

    public static void insertCourse(String tableName, ResultSet courseResultSet, Statement customScheduleStatement) {
        try {
            String addCourseSQLSTRING = "INSERT INTO \"" + tableName + "\" VALUES (" +
                    courseResultSet.getInt(1) + ", " +
                    "'" + courseResultSet.getString(2) + "', " +
                    "'" + courseResultSet.getString(3) + "', " +
                    courseResultSet.getInt(4) + ", " +
                    courseResultSet.getInt(5) + ", " +
                    "'" + courseResultSet.getString(6).charAt(0) + "', " +
                    courseResultSet.getInt(7) + ", " +
                    "'" + courseResultSet.getString(8) + "', " +
                    "'" + courseResultSet.getString(9) + "')";
            customScheduleStatement.executeUpdate(addCourseSQLSTRING);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught SQLException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
    }

}
