package com.rjew.ArenaScheduler;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class CourseTest {

    @Test
    public void defaultConstructorShouldSetCorrectValues() {
        Course course = new Course();

        assertEquals((Integer) 0, course.getSubjectID());
        assertEquals("", course.getCourseID());
        assertEquals("", course.getCourseTitle());
        assertEquals((Integer) 0, course.getClassID());
        assertEquals((Integer) 0, course.getSeats());
        assertEquals((Character) 'Z', course.getCode());
        assertEquals((Integer) 0, course.getBlock());
        assertEquals("", course.getRoom());
        assertEquals("", course.getTeacher());
    }

    @Test
    public void compareToShouldReturnCorrectValues() {
        Course course1 = new Course();
        Course course2 = new Course();

        assertEquals(0, course1.compareTo(course2));

        course1.setBlock(1);
        course2.setBlock(5);

        assertEquals(-1, course1.compareTo(course2));
        assertEquals(1, course2.compareTo(course1));
    }

    @Test
    public void equalsShouldReturnCorrectValues() {
        Course course1 = new Course();
        Course course2 = new Course();

        assertTrue(course1.equals(course2));

        course2.setClassID(301);

        assertFalse(course1.equals(course2));

        course1.setClassID(null);//One null

        assertFalse(course1.equals(course2));

        course2.setClassID(null);//Both null

        assertFalse(course1.equals(course2));

        course1.setClassID(2);//Other null

        assertFalse(course1.equals(course2));

        Object object = new Object();

        assertFalse(course1.equals(object));
    }
}
