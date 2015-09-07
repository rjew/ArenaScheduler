package com.rjew.ArenaScheduler;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Utilities for collecting user input
 */
class ScannerUtils {

    /**
     * To prevent instantiation
     */
    private ScannerUtils() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    private static final Scanner keyboard = new Scanner(System.in);

    /**
     * Returns an int from standard input, ignoring input that is not an int
     * @return An int from standard input
     */
    public static int getInt() {
        int anInt = 0;

        try {
            anInt = keyboard.nextInt();
        } catch (InputMismatchException ex) {
            //ignore exception, prompt user again for input if input is incorrect
        }
        keyboard.nextLine();//clear the buffer

        return anInt;
    }

    /**
     * Returns A String from standard input, prompting again for blank spaces or lines
     * @return A String from standard input
     */
    public static String getString() {
        String string;

        do {
             string = keyboard.nextLine();
        } while(string.trim().isEmpty());//Check if string is empty

        return string;
    }
}
