package com.rjew.ArenaScheduler;

import java.io.Serializable;

public class Course implements Serializable, Comparable<Course> {
    private Integer subjectID;
    private String courseID;
    private String courseTitle;
    private Integer classID;
    private Integer seats;
    private Character code;
    private Integer block;
    private String room;
    private String teacher;

    public Course() {
        this.subjectID = 0;
        this.courseID = "";
        this.courseTitle = "";
        this.classID = 0;
        this.seats = 0;
        this.code = 'Z';
        this.block = 0;
        this.room = "";
        this.teacher = "";
    }

    public Course(Integer subjectID, String courseID, String courseTitle,
                  Integer classID, Integer seats, Character code,
                  Integer block, String room, String teacher) {
        this.subjectID = subjectID;
        this.courseID = courseID;
        this.courseTitle = courseTitle;
        this.classID = classID;
        this.seats = seats;
        this.code = code;
        this.block = block;
        this.room = room;
        this.teacher = teacher;
    }

    public Course(Course course) {
        this.subjectID = course.subjectID;
        this.courseID = course.courseID;
        this.courseTitle = course.courseTitle;
        this.classID = course.classID;
        this.seats = course.seats;
        this.code = course.code;
        this.block = course.block;
        this.room = course.room;
        this.teacher = course.teacher;
    }


    //Default compares courses by block
    @Override
    public int compareTo(Course course) {
        if (this.block < course.block) {
            return -1;
        } else if (this.block > course.block) {
            return 1;
        } else {
            return 0;
        }
    }


    public Integer getSubjectID() {
        return subjectID;
    }

    public String getCourseID() {
        return courseID;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public Integer getClassID() {
        return classID;
    }

    public Integer getSeats() {
        return seats;
    }

    public Character getCode() {
        return code;
    }

    public Integer getBlock() {
        return block;
    }

    public String getRoom() {
        return room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setSubjectID(Integer subjectID) {
        this.subjectID = subjectID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setClassID(Integer classID) {
        this.classID = classID;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public void setCode(Character code) {
        this.code = code;
    }

    public void setBlock(Integer block) {
        this.block = block;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    /**
     * The courseID is unique for each Course. So this should compare Course by courseID only.
     */
    @Override
    public boolean equals(Object other) {
        return (other instanceof Course) && (courseID != null)
                ? courseID.equals(((Course) other).getCourseID())
                : (other == this);
    }

    /**
     * The courseID is unique for each Course. So a Course with same courseID should return same hashcode.
     */
    @Override
    public int hashCode() {
        return (courseID != null)
                ? (this.getClass().hashCode() + courseID.hashCode())
                : super.hashCode();
    }

    /**
     * Returns the String representation of this Course.
     */
    @Override
    public String toString() {
        return String.format("Course[subjectID=%d,courseID=%s,courseTitle=%s,classID=%d,seats=%d," +
                        "code=%c,block=%d,room=%s,teacher=%s]",
                subjectID, courseID, courseTitle, classID, seats, code, block, room, teacher);
    }
}
