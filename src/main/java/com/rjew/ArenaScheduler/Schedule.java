package com.rjew.ArenaScheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Schedule implements Serializable {
    private ArrayList<Course> courseArrayList;
    public static final int COURSE_LIMIT = 7;
    public static final int LAST_BLOCK = 8;
    private StringBuilder name;

    public Schedule() {
        courseArrayList = new ArrayList<Course>();
        name = new StringBuilder();
    }

    public Schedule(Schedule schd) {
        courseArrayList = new ArrayList<Course>();

        for (int i = 0; i < courseArrayList.size(); i++) {
            this.courseArrayList.add(schd.courseArrayList.get(i));
        }
        this.name = schd.name;
    }

    public void addCourse(Course crs) {
        boolean sameBlock = false;
        boolean courseLimitReached = false;
        int numCourses = 0;

        for (int i = 0; i < courseArrayList.size(); i++) {
            if (courseArrayList.get(i).getBlock() <= LAST_BLOCK) {
                numCourses++;
            }

            if (courseArrayList.get(i).getBlock() == crs.getBlock()) {
                sameBlock = true;
            }
        }

        if (numCourses >= COURSE_LIMIT) {
            courseLimitReached = true;
        }

        if (sameBlock == false && courseLimitReached == false) {
            courseArrayList.add(crs);
        }
    }

    public Course getCourse(int block) {
        for (int i = 0; i < courseArrayList.size(); i++) {
            if (courseArrayList.get(i).getBlock() == block) {
                return courseArrayList.get(i);
            }
        }

        return new Course();
    }

    public void removeCourse(int block) {
        for (int i = 0; i < courseArrayList.size(); i++) {
            if (courseArrayList.get(i).getBlock() == block) {
                courseArrayList.remove(i);
            }
        }
    }

    public void changeName(String string) {
        if (name.length() == 0) {
            name.append(string);
        } else {
            name.replace(0, name.length() - 1, string);
        }
    }

    public String getName() {
        return name.toString();
    }

    public void sortScheduleByBlock() {
        Collections.sort(courseArrayList, new BlockCompare());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(name).append("\n");

        for (int i = 1; i <= courseArrayList.size(); i++) {
            stringBuilder.append("Course ").append(i).append(" :\n");
            stringBuilder.append(courseArrayList.get(i - 1).toString());
        }

        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}