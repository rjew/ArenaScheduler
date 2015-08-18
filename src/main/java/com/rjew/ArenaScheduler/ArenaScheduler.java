package com.rjew.ArenaScheduler;

public class ArenaScheduler {
    public static void main(String[] args ) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

            MainMenu.displayArenaSchedulerMenu();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}