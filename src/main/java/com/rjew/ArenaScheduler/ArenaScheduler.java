package com.rjew.ArenaScheduler;

import java.sql.*;
import java.io.*;
import java.util.*;

public class ArenaScheduler {
    static final Map<Integer , String> SUBJECT_ID = new HashMap<Integer , String>() {{
        put(1, "Math");
        put(2, "Science");
        put(3, "English");
        put(4, "SocialSci");
        put(5, "VPA");
        put(6, "WorldLang");
        put(7, "PE");
        put(8, "Other");
    }};

    public static void main(String[] args ) {
        final String DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Announcer_Fall_2015";

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

            Connection conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection created to CoffeeDB.");

            displayArenaSchedulerMenu();
        }
        catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    public enum SubjectID {
        MATH, SCIENCE, ENGLISH, SOCIALSCI, VPA, WORLDLANG, PE, OTHER;
    }

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

    public class BlockCompare implements Comparator<Course> {
        public int compare(Course c1, Course c2) {
            if (c1.block < c2.block) {
                return -1;
            } else if (c1.block > c2.block) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public class TeacherCompare implements Comparator<Course> {
        public int compare(Course c1, Course c2) {
            return c1.getTeacher().compareTo(c2.getTeacher());
        }
    }

    public class Schedule implements Serializable {
        private ArrayList<Course> courseArrayList = new ArrayList<Course>();
        public static final int COURSE_LIMIT = 7;
        public static final int LAST_BLOCK = 8;

        /*public Schedule() {
            ;
        }*/

        public Schedule(Schedule schd) {
            for (int i = 0; i < courseArrayList.size(); i++) {
                this.courseArrayList.add(schd.courseArrayList.get(i));
            }
        }

        public void addCourse(Course crs) {
            boolean sameBlock = false;
            boolean courseLimitReached = false;
            int numCourses = 0;

            for (int i = 0; i < courseArrayList.size(); i++) {
                if (courseArrayList.get(i).getBlock() <= LAST_BLOCK) {
                    numCourses++;
                }

                if (courseArrayList.get(i).getBlock() == crs.getBlock()) {
                    sameBlock = true;
                }
            }

            if (numCourses >= COURSE_LIMIT) {
                courseLimitReached = true;
            }

            if (sameBlock == false && courseLimitReached == false) {
                courseArrayList.add(crs);
            }
        }

        public Course getCourse(int block) {
            for (int i = 0; i < courseArrayList.size(); i++) {
                if (courseArrayList.get(i).getBlock() == block) {
                    return courseArrayList.get(i);
                }
            }

            return new Course();
        }

        public void removeCourse(int block) {
            for (int i = 0; i < courseArrayList.size(); i++) {
                if (courseArrayList.get(i).getBlock() == block) {
                    courseArrayList.remove(i);
                }
            }
        }

        @Override
        public String toString() {
            Collections.sort(courseArrayList);
            return super.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }

    public static void displayArenaSchedulerMenu() {
        int option = 0;

        Scanner keyboard = new Scanner(System.in);

        /* Print the Menu */
        do {
            System.out.println("Welcome to the Arena Scheduler Program!\n"
                    + "What would you like to do?\n"
                    + "(1) Search Catalog\n"
                    + "(2) View your custom schedules\n"
                    + "(3) Quit\n"
                    + "Pick an option (1-3) and press ENTER.");
            try {
                option = keyboard.nextInt();
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }

            switch (option) {
                case 1:
                    displaySearchCatalog(keyboard);
                    break;
                case 2:
                    viewCustomSchedules();
                    break;
                case 3:
                    break;
                default:
                    System.out.println("WRONG OPTION!");
            }
        } while (option != 3);

        keyboard.close();
    }

    public static void displaySearchCatalog(Scanner keyboard) {
        int switchOption; //To hold the option for the right switch statement case
        int menuOption = 0; // To hold the users option based on the displayed menu
        String searchOptionString; //To hold user input for search catalog for strings
        int searchOptionInt; //To hold user input for search catalog for ints
        StringBuilder sqlStatement = new StringBuilder("SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk");

        ArrayList<String> menuItems = new ArrayList<String>(); //To create a dynamic menu based on previous choices
        menuItems.add("1Search by SubjectID");
        menuItems.add("2Search by CourseID");
        menuItems.add("3Search by ClassID");
        menuItems.add("4Search by Block");
        menuItems.add("5Search by Teacher");

        /* Print the menu */
        do {
            do {
                System.out.println("What would you like to search for?");
                for(int i = 1; i <= menuItems.size(); i++) { //Print all available menu items
                    System.out.println("(" + i + ") " + menuItems.get(i-1).substring(1));
                }
                try {
                    menuOption = keyboard.nextInt();
                } catch (Exception ex) {
                    System.out.println("ERROR: " + ex.getMessage());
                }

                if (menuOption < 1 || menuOption > menuItems.size()) { //If the user's choice is out of bounds
                    System.out.println("WRONG OPTION!");
                }
            } while (menuOption < 1 || menuOption > menuItems.size());

            /* Convert the user's menu option to the correct switch case */
            switchOption = Character.getNumericValue(menuItems.get(menuOption - 1).charAt(0));

            /* Give menu options */
            switch (switchOption) {
                case 1:
                    System.out.print("Enter the Subject ID: ");
                    searchOptionInt = keyboard.nextInt();
                    sqlStatement.append(" AND subject_id = ");
                    sqlStatement.append(searchOptionInt);
                    break;
                case 2:
                    System.out.print("Enter the Course ID: ");
                    keyboard.nextLine(); //Consume the newline
                    searchOptionString = keyboard.nextLine();
                    sqlStatement.append(" AND LOWER(course_id_fk) LIKE LOWER('");
                    sqlStatement.append(searchOptionString);
                    sqlStatement.append("')");
                    break;
                case 3:
                    System.out.print("Enter the Class ID: ");
                    searchOptionInt = keyboard.nextInt();
                    sqlStatement.append(" AND class_id = ");
                    sqlStatement.append(searchOptionInt);
                    break;
                case 4:
                    System.out.print("Enter the Block: ");
                    searchOptionInt = keyboard.nextInt();
                    sqlStatement.append(" AND block = ");
                    sqlStatement.append(searchOptionInt);
                    break;
                case 5:
                    System.out.print("Enter the Teacher: ");
                    keyboard.nextLine(); //Consume the newline
                    searchOptionString = keyboard.nextLine();
                    sqlStatement.append(" AND LOWER(teacher) LIKE LOWER('%");
                    sqlStatement.append(searchOptionString);
                    sqlStatement.append("%')");
                    break;
                }

            menuItems.remove(menuOption - 1); //Remove the menu option that the user picked from the display

            /* Prompt the user for another search parameter */
            if (menuItems.size() != 0) { //Check to see if all parameters are already taken
                do {
                    System.out.println("Would you like to add another search parameter?\n" +
                            "(1) Yes\n" +
                            "(2) No");
                    try {
                        switchOption = keyboard.nextInt();
                    } catch (Exception ex) {
                        System.out.println("ERROR: " + ex.getMessage());
                    }
                    if (switchOption < 1 || switchOption > 2) { //Out of bounds option
                        System.out.println("WRONG OPTION!");
                    }
                } while (switchOption < 1 || switchOption > 2);
            } else { //Exit menu if all parameters are taken
                switchOption = 2;
            }
        } while(switchOption == 1);

        executeSQLStatement(sqlStatement.toString());
    }

    public static void executeSQLStatement(String sqlStmt) {
        final String DB_URL = "jdbc:derby:/opt/squirrel-sql-3.6/Announcer_Fall_2015"; //For the db connection
        int numRows; //To hold the number of rows, the number of results

        try {

            /* Connect to database and execute query, storing the results */
            Connection conn = DriverManager.getConnection(DB_URL);

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet resultSet = stmt.executeQuery(sqlStmt);

            ResultSetMetaData meta = resultSet.getMetaData();

            /* Get the number of results */
            resultSet.last();
            numRows = resultSet.getRow();
            resultSet.first();

            System.out.println("\n" + numRows + " RESULTS.\n");

            if (numRows != 0) {
                int courseTitleFormatWidth = meta.getColumnName(3).length() + 1; //Store the default width of the course title
                int roomFormatWidth = meta.getColumnName(8).length() + 1;//Store the default width of the room

                /* If one of the course titles has a longer name, store it for the width */
                do {
                    if (resultSet.getString(3).length() + 1 > courseTitleFormatWidth) {
                        courseTitleFormatWidth = resultSet.getString(3).length() + 1;
                    }
                } while (resultSet.next());

                /* Roll back the resultSet */
                resultSet.first();

                /* If one of the rooms has a longer name, store it for the width */
                do {
                    if (resultSet.getString(8).length() + 1 > roomFormatWidth) {
                        roomFormatWidth = resultSet.getString(8).length() + 1;
                    }
                } while (resultSet.next());

                /* Roll back the resultSet */
                resultSet.first();

                /* Print out the column titles */
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    switch (i) {
                        case 1:
                            System.out.printf("%-11s", meta.getColumnName(i) + " ");
                            break;
                        case 2:
                            System.out.printf("%-10s", meta.getColumnName(i) + " ");
                            break;
                        case 3:
                            System.out.printf("%-" + courseTitleFormatWidth + "s", meta.getColumnName(i) + " ");
                            break;
                        case 4:
                            System.out.printf("%-9s", meta.getColumnName(i) + " ");
                            break;
                        case 5:
                            System.out.printf("%-6s", meta.getColumnName(i) + " ");
                            break;
                        case 6:
                            System.out.printf("%-5s", meta.getColumnName(i) + " ");
                            break;
                        case 7:
                            System.out.printf("%-6s", meta.getColumnName(i) + " ");
                            break;
                        case 8:
                            System.out.printf("%-" + roomFormatWidth + "s", meta.getColumnName(i) + " ");
                            break;
                        case 9:
                            System.out.print(meta.getColumnName(i));
                            break;
                    }
                }

                System.out.println();

                /* Print out the database results */
                do {
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        switch (i) {
                            case 1:
                                System.out.printf("%-11s", resultSet.getString(i) + " ");
                                break;
                            case 2:
                                System.out.printf("%-10s", resultSet.getString(i) + " ");
                                break;
                            case 3:
                                System.out.printf("%-" + courseTitleFormatWidth + "s", resultSet.getString(i) + " ");
                                break;
                            case 4:
                                System.out.printf("%-9s", resultSet.getString(i) + " ");
                                break;
                            case 5:
                                System.out.printf("%-6s", resultSet.getString(i) + " ");
                                break;
                            case 6:
                                System.out.printf("%-5s", resultSet.getString(i) + " ");
                                break;
                            case 7:
                                System.out.printf("%-6s", resultSet.getString(i) + " ");
                                break;
                            case 8:
                                System.out.printf("%-" + roomFormatWidth + "s", resultSet.getString(i) + " ");
                                break;
                            case 9:
                                System.out.print(resultSet.getString(i));
                                break;
                        }
                    }
                    System.out.println();
                } while (resultSet.next());

            }

            stmt.close();
            conn.close();
            System.out.println("\nConnection closed.");
        }
        catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    public static void viewCustomSchedules() {
        ;
    }
}