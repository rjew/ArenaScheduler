package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomScheduleManagerAddCourse {

    final static Logger logger = Logger.getLogger(CustomScheduleManagerAddCourse.class);

    /**
     * Responsible for adding a course to a schedule that the user specifies.
     * @param classID The Class ID of the course to be added to a schedule
     * @param tableName The name of the schedule where the class will be added
     * @param announcerStatement The Statement for the announcer for executing sql queries
     * @param customScheduleStatement The Statement for custom schedules to execute sql queries
     * @return a boolean value indicating whether the class was added successfully or not,
     * true=successfully added, false=not successful
     */
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
                    System.out.print("\nCannot add course. ");

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

    /**
     * Checks if the course limit for a schedule has already been reached
     * @param tableName The name of the schedule to check
     * @param customScheduleStatement The Statement for the custom schedule for executing sql queries
     * @return a boolean value indicating whether or not the course limit has been reached,
     * true=limit reached, false=limit not reached
     */
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

    /**
     * Checks if the course being added has the same block as one of the courses in the schedule
     * @param tableName Schedule name that the course is being added to
     * @param customScheduleStatement Statement for the custom schedules for sql queries
     * @param courseBlock The block of the course being added
     * @return a boolean value indicating whether a course with the same block exists in the schedule,
     * true=exists a course with the same block, false=does not exist a course with the same block
     */
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

    /**
     * Responsible for inserting a course into a specified schedule
     * @param tableName The schedule for the course to be inserted to
     * @param courseResultSet The Result Set for the course being added to the schedule, gives the info of the values to be inserted
     * @param customScheduleStatement Statement for the custom schedules for executing sql queries
     */
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
