package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

class ArenaScheduler {

    private final static Logger logger = Logger.getLogger(ArenaScheduler.class);

    public static void main(String[] args ) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

            MainMenu.displayArenaSchedulerMenu();
        } catch (ClassNotFoundException ex) {
            logger.error("Class could not be found.", ex);
            System.err.println("ERROR: Caught ClassNotFoundException: " + ex.getMessage());
        } catch (InstantiationException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught InstantiationException: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            logger.error(ex);
            System.err.println("ERROR: Caught IllegalAccessException: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            System.err.println("ERROR: " + ex.getMessage());
        }
    }
}