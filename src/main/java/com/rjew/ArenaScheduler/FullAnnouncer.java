package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.util.Scanner;

public class FullAnnouncer {

    final static Logger logger = Logger.getLogger(FullAnnouncer.class);

    public static void viewFullAnnouncer(Scanner keyboard) {
        String viewFullAnnouncerSQLString = "SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk";

        ExecuteSQL.executeSQLStatement(viewFullAnnouncerSQLString, keyboard);
    }
}
