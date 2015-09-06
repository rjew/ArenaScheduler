package com.rjew.ArenaScheduler;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ScannerUtils {

    /**
     * To prevent instantiation
     */
    private ScannerUtils() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static final Scanner keyboard = new Scanner(System.in);

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

    public static String getString() {
        String string;

        do {
             string = keyboard.nextLine();
        } while(string.trim().isEmpty());//Check if string is empty

        return string;
    }
}
