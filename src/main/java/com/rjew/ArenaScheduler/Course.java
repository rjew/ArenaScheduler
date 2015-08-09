package com.rjew.ArenaScheduler;

import java.io.Serializable;
import java.sql.ResultSet;

public class Course implements Serializable, Comparable<Course> {
    private int subjectID;
    private String courseID;
    private String courseTitle;
    private int classID;
    private int seats;
    private char code;
    private int block;
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

    public Course(int subjectID, String courseID, String courseTitle,
                  int classID, int seats, char code,
                  int block, String room, String teacher) {
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

    public Course(Course crs) {
        this.subjectID = crs.subjectID;
        this.courseID = crs.courseID;
        this.courseTitle = crs.courseTitle;
        this.classID = crs.classID;
        this.seats = crs.seats;
        this.code = crs.code;
        this.block = crs.block;
        this.room = crs.room;
        this.teacher = crs.teacher;
    }

    public Course(ResultSet resultSet) {
        try {
            resultSet.first();
            this.subjectID = resultSet.getInt(1);
            this.courseID = resultSet.getString(2);
            this.courseTitle = resultSet.getString(3);
            this.classID = resultSet.getInt(4);
            this.seats = resultSet.getInt(5);
            this.code = resultSet.getString(6).charAt(0);
            this.block = resultSet.getInt(7);
            this.room = resultSet.getString(8);
            this.teacher = resultSet.getString(9);
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    //@Override
    public int compareTo(Course crs) {
        if (this.block < crs.block) {
            return -1;
        } else if (this.block > crs.block) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getSubjectID() {
        return subjectID;
    }

    public String getCourseID() {
        return courseID;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public int getClassID() {
        return classID;
    }

    public int getSeats() {
        return seats;
    }

    public char getCode() {
        return code;
    }

    public int getBlock() {
        return block;
    }

    public String getRoom() {
        return room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public void setCode(char code) {
        this.code = code;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        String s;

        s = "subjectID: " + subjectID +
                "\ncourseID: " + courseID +
                "\ncourseTitle: " + courseTitle +
                "\ncourseID: " + courseID +
                "\nseats: " + seats +
                "\ncode: " + code +
                "\nblock: " + block +
                "\nroom: " + room +
                "\nteacher: " + teacher;

        return s;
    }

    //@Override
    public boolean equals(Course crs) {
        if (this.subjectID == crs.subjectID
                && this.courseID.equals(crs.courseID)
                && this.courseTitle.equals(crs.courseTitle)
                && this.classID == crs.classID
                && this.seats == crs.seats
                && this.code == crs.code
                && this.block == crs.block
                && this.room.equals(crs.room)
                && this.teacher.equals(crs.teacher)) {
            return true;
        } else {
            return false;
        }
    }
}