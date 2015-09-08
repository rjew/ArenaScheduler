package com.rjew.ArenaScheduler;

import java.util.Comparator;

public class TeacherCompare implements Comparator<Course> {
    public int compare(Course c1, Course c2) {
        return c1.getTeacher().compareTo(c2.getTeacher());
    }
}