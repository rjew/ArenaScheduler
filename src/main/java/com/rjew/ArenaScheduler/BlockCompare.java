package com.rjew.ArenaScheduler;

import java.util.Comparator;

public class BlockCompare implements Comparator<Course> {
    public int compare(Course c1, Course c2) {
        if (c1.getBlock() < c2.getBlock()) {
            return -1;
        } else if (c1.getBlock() > c2.getBlock()) {
            return 1;
        } else {
            return 0;
        }
    }
}
