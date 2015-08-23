package com.rjew.ArenaScheduler;

public class ArenaScheduler {
    public static void main(String[] args ) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

            MainMenu.displayArenaSchedulerMenu();
        } catch (ClassNotFoundException ex) {
            System.out.println("Class not found.");//todo fix this exception
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}