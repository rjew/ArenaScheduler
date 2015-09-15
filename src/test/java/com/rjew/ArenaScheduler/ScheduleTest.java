package com.rjew.ArenaScheduler;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.powermock.reflect.Whitebox;

public class ScheduleTest {

    private Schedule schedule1;

    @Before
    public void setUp() {
        schedule1 = new Schedule();

        //Suppress System.out.print
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                //No Output
            }
        }));
    }

    @Test
    public void constructorShouldSetCorrectValues() {
        Course course1 = new Course();
        course1.setClassID(23);
        course1.setBlock(1);
        schedule1.addCourse(course1);

        Course course2 = new Course();
        course2.setClassID(621);
        course1.setBlock(2);
        schedule1.addCourse(course2);

        Schedule schedule2 = new Schedule(schedule1, "schedule 2", 2);

        assertEquals(course1, schedule2.getCourseList().get(0));
        assertEquals(course2, schedule2.getCourseList().get(1));
        assertEquals("schedule 2", schedule2.getName());
        assertEquals((Integer) 2, schedule2.getRank());
    }

    @Test
    public void compareToShouldReturnCorrectValues() {
        Schedule schedule2 = new Schedule();

        schedule1.setRank(1);
        schedule2.setRank(1);

        assertEquals(0, schedule1.compareTo(schedule2));

        schedule2.setRank(5);

        assertEquals(-1, schedule1.compareTo(schedule2));
        assertEquals(1, schedule2.compareTo(schedule1));
    }

    @Test
    public void addCorrectCourseShouldReturnTrue() {
        Course course = new Course();
        course.setClassID(1);
        course.setBlock(1);

        assertTrue("Course should be able to be added", schedule1.addCourse(course));
    }

    @Test
    public void checkCourseLimitShouldReturnFalse() {
        List<Course> courseList = new ArrayList<>();

        for (int i = 1; i < 8; i++) {
            Course course = new Course();
            course.setBlock(i);
            course.setClassID(i);
            courseList.add(course);
        }

        schedule1.setCourseList(courseList);

        Course course = new Course();
        course.setBlock(8);
        course.setClassID(8);

        assertFalse("Course limit should be reached", schedule1.addCourse(course));
    }

    @Test
    public void invalidClassIDShouldReturnFalse() {
        Course course = new Course();

        assertFalse("Should be invalid class ID", schedule1.addCourse(course));
    }

    @Test
    public void checkSameBlockShouldReturnFalse() {
        Course course1 = new Course();
        course1.setBlock(1);
        course1.setClassID(1);

        schedule1.addCourse(course1);

        Course course2 = new Course();
        course2.setBlock(1);
        course2.setClassID(2);

        assertFalse("Should prevent adding same block", schedule1.addCourse(course2));
    }

    @Test
    public void deleteCourseValidClassIDShouldReturnTrue() {
        List<Course> courseList = new ArrayList<>();
        Course course1 = new Course();
        Course course2 = new Course();
        Course course3 = new Course();

        course1.setClassID(1);
        courseList.add(course1);

        course2.setClassID(5);
        courseList.add(course2);

        course3.setClassID(3);
        courseList.add(course3);

        schedule1.setCourseList(courseList);

        assertTrue("Should delete the valid class ID", schedule1.deleteCourse(5));
    }

    @Test
    public void deleteCourseInvalidClassIDShouldReturnFalse() {
        List<Course> courseList = new ArrayList<>();
        Course course1 = new Course();
        Course course2 = new Course();
        Course course3 = new Course();

        course1.setClassID(1);
        courseList.add(course1);

        course2.setClassID(5);
        courseList.add(course2);

        course3.setClassID(3);
        courseList.add(course3);

        schedule1.setCourseList(courseList);

        assertFalse("Should not be able to delete class", schedule1.deleteCourse(2));
    }

    @Test
    public void deleteValidCourseShouldReturnTrue() {
        List<Course> courseList = new ArrayList<>();
        Course course1 = new Course();
        Course course2 = new Course();
        Course course3 = new Course();

        course1.setClassID(1);
        courseList.add(course1);

        course2.setClassID(5);
        courseList.add(course2);

        course3.setClassID(3);
        courseList.add(course3);

        schedule1.setCourseList(courseList);

        assertTrue("Should delete the valid class ID", schedule1.deleteCourse(course2));
    }

    @Test
    public void deleteInvalidCourseShouldReturnFalse() {
        List<Course> courseList = new ArrayList<>();
        Course course1 = new Course();
        Course course2 = new Course();
        Course course3 = new Course();
        Course course4 = new Course();

        course1.setClassID(1);
        courseList.add(course1);

        course2.setClassID(5);
        courseList.add(course2);

        course3.setClassID(3);
        courseList.add(course3);

        schedule1.setCourseList(courseList);

        course4.setClassID(2);

        assertFalse("Should not be able to delete class", schedule1.deleteCourse(course4));
    }

    @Test
    public void getCourseTitleFormatWidthWithEmptyCourseListShouldReturnCorrectValue() throws Exception {
        List<Course> courseList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Course course = new Course();
            courseList.add(course);
        }

        int courseTitleFormatWidth = Whitebox.invokeMethod(schedule1, "getCourseTitleFormatWidth", courseList);

        assertEquals(13, courseTitleFormatWidth);
    }

    @Test
    public void getRoomFormatWidthWithEmptyCourseListShouldReturnCorrectValue() throws Exception {
        List<Course> courseList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Course course = new Course();
            courseList.add(course);
        }

        int roomFormatWidth = Whitebox.invokeMethod(schedule1, "getRoomFormatWidth", courseList);

        assertEquals(5, roomFormatWidth);
    }

    @Test
    public void getCourseTitleFormatWidthWithFilledCourseListShouldReturnCorrectValue() throws Exception {
        List<Course> courseList = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("aaaaaaaaa");//length 9

        for (int i = 0; i < 5; i++) {
            Course course = new Course();
            stringBuilder.append("a");
            course.setCourseTitle(stringBuilder.toString());
            courseList.add(course);
        }

        int courseTitleFormatWidth = Whitebox.invokeMethod(schedule1, "getCourseTitleFormatWidth", courseList);

        assertEquals(15, courseTitleFormatWidth);
    }

    @Test
    public void getRoomFormatWidthWithFilledCourseListShouldReturnCorrectValue() throws Exception {
        List<Course> courseList = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("aaaaaaaaa");//length 9

        for (int i = 0; i < 5; i++) {
            Course course = new Course();
            stringBuilder.append("a");
            course.setRoom(stringBuilder.toString());
            courseList.add(course);
        }

        int roomFormatWidth = Whitebox.invokeMethod(schedule1, "getRoomFormatWidth", courseList);

        assertEquals(15, roomFormatWidth);
    }

    @Test
    public void equalsShouldReturnCorrectValues() {
        Schedule schedule2 = new Schedule();

        schedule1.setName("schedule1");
        schedule2.setName("schedule1");

        assertTrue(schedule1.equals(schedule2));

        schedule2.setName("schedule2");

        assertFalse(schedule1.equals(schedule2));

        schedule1.setName(null);//One null

        assertFalse(schedule1.equals(schedule2));

        schedule2.setName(null);//Both null

        assertFalse(schedule1.equals(schedule2));

        schedule1.setName("schedule1");//Other null

        assertFalse(schedule1.equals(schedule2));

        Object object = new Object();

        assertFalse(schedule1.equals(object));
    }
}
