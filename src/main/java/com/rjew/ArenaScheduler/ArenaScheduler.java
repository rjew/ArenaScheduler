package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;

import java.sql.SQLException;

class ArenaScheduler {

    private final static Logger logger = Logger.getLogger(ArenaScheduler.class);

    public static void main(String[] args ) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            Menu.displayArenaSchedulerMenu();
        } catch (ClassNotFoundException ex) {
            logger.error("ClassNotFoundException caught.", ex);
            System.err.println("ERROR - ClassNotFoundException: " + ex.toString());
        } catch (InstantiationException ex) {
            logger.error("InstantiationException caught.", ex);
            System.err.println("ERROR - InstantiationException: " + ex.toString());
        } catch (IllegalAccessException ex) {
            logger.error("IllegalAccessException caught.", ex);
            System.err.println("ERROR - IllegalAccessException: " + ex.toString());
        } catch (IndexOutOfBoundsException ex) {
            logger.error("IndexOutOfBoundsException caught", ex);
            System.err.print("ERROR - IndexOutOfBoundsException: " + ex.toString());
        } catch (SQLException ex) {
            logger.error("SQLException caught", ex);
            System.err.print("ERROR - SQLException: " + ex.toString());
        } catch (Exception ex) {
            logger.error("Exception caught", ex);
            System.err.print("ERROR - Exception: " + ex.toString());
        }
    }
}