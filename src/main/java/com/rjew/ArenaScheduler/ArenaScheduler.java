package com.rjew.ArenaScheduler;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

class ArenaScheduler {

    private final static Logger logger = Logger.getLogger(ArenaScheduler.class);

    public static void main(String[] args ) {
        try {
            PropertyConfigurator.configure(ArenaScheduler.class.getClassLoader().getResource("log4j.properties"));
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            Menu.getArenaSchedulerMenu();
        } catch (ClassNotFoundException ex) {
            logger.error("ClassNotFoundException caught.", ex);
            System.err.println("ERROR - ClassNotFoundException: " + ex.toString());
            ScannerUtils.pressEnterToContinue();
        } catch (InstantiationException ex) {
            logger.error("InstantiationException caught.", ex);
            System.err.println("ERROR - InstantiationException: " + ex.toString());
            ScannerUtils.pressEnterToContinue();
        } catch (IllegalAccessException ex) {
            logger.error("IllegalAccessException caught.", ex);
            System.err.println("ERROR - IllegalAccessException: " + ex.toString());
            ScannerUtils.pressEnterToContinue();
        } catch (IndexOutOfBoundsException ex) {
            logger.error("IndexOutOfBoundsException caught", ex);
            System.err.print("ERROR - IndexOutOfBoundsException: " + ex.toString());
            ScannerUtils.pressEnterToContinue();
        } catch (NullPointerException ex) {
            logger.error("NullPointerException caught", ex);
            System.err.print("ERROR - NullPointerException: " + ex.toString());
            ScannerUtils.pressEnterToContinue();
        } catch (FileNotFoundException ex) {
            logger.error("FileNotFoundException caught", ex);
            System.err.print("ERROR - FileNotFoundException: " + ex.toString());
            ScannerUtils.pressEnterToContinue();
        } catch (IOException ex) {
            logger.error("IOException caught", ex);
            System.err.print("ERROR - IOException: " + ex.toString());
            ScannerUtils.pressEnterToContinue();
        } catch (SQLException ex) {
            logger.error("SQLException caught", ex);
            System.err.print("ERROR - SQLException: " + ex.toString());
            ScannerUtils.pressEnterToContinue();
        } catch (Exception ex) {
            logger.error("Exception caught", ex);
            System.err.print("ERROR - Exception: " + ex.toString());
            ScannerUtils.pressEnterToContinue();
        }
    }
}