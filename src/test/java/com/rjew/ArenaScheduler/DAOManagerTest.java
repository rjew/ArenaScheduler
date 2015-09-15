package com.rjew.ArenaScheduler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;

/**
 * Cannot run at same time as other tests because it accesses the derby database
 */
public class DAOManagerTest {
    private DAOManager daoManager = new DAOManager("jdbc:derby:Announcer_Fall_2015");

    @Test
    public void executeSelectQueryShouldReturnCorrectCourseList() throws SQLException {
        String printFullAnnouncerSQLString = "SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk";

        List<Course> courseList = daoManager.executeSelectQuery(printFullAnnouncerSQLString);

        assertEquals(648, courseList.size());
    }

    @Test
    public void validCourseIDShouldReturnValidCourse() throws SQLException {
        Course course = new Course(1, "ALGC151A", "Algebra 1A", 1 , 32 , 'B', 1, "256", "Hong");

        assertEquals(course, daoManager.getCourse(1));
    }

    @Test
    public void validCourseIDShouldReturnEmptyCourse() throws SQLException {
        assertEquals(new Course(), daoManager.getCourse(99999));
    }
}
