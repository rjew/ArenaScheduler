package com.rjew.ArenaScheduler;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DAOUtilsTest {
    @Test
    public void getCourseTitleFormatWidthWithEmptyCourseListShouldReturnCorrectValue() throws Exception {
        List<Course> courseList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Course course = new Course();
            courseList.add(course);
        }

        int courseTitleFormatWidth = Whitebox.invokeMethod(DAOUtils.class, "getCourseTitleFormatWidth", courseList);

        assertEquals(13, courseTitleFormatWidth);
    }

    @Test
    public void getRoomFormatWidthWithEmptyCourseListShouldReturnCorrectValue() throws Exception {
        List<Course> courseList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Course course = new Course();
            courseList.add(course);
        }

        int roomFormatWidth = Whitebox.invokeMethod(DAOUtils.class, "getRoomFormatWidth", courseList);

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

        int courseTitleFormatWidth = Whitebox.invokeMethod(DAOUtils.class, "getCourseTitleFormatWidth", courseList);

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

        int roomFormatWidth = Whitebox.invokeMethod(DAOUtils.class, "getRoomFormatWidth", courseList);

        assertEquals(15, roomFormatWidth);
    }
}
