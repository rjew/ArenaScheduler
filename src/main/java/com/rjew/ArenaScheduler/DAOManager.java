package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOManager {
    private String database_URL;

    public DAOManager(String database_URL) {
        this.database_URL = database_URL;
    }

    public List<Course> executeSearchCatalogQuery(String sqlStmt, ArrayList<Object> preparedStatementParameters)
            throws SQLException {
        List<Course> courseList;

        try (Connection connection = DriverManager.getConnection(database_URL);
             PreparedStatement preparedStatement =
                     connection.prepareStatement(sqlStmt, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
        ) {

            for (int i = 0; i < preparedStatementParameters.size(); i++) {
                preparedStatement.setObject(i + 1, preparedStatementParameters.get(i));
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                courseList = DAOUtils.resultSetToList(resultSet);
                return courseList;
            }
        }
    }

    public List<Course> executeSelectQuery(String selectSQLString) throws SQLException {
        List<Course> courseList;

        try (Connection connection = DriverManager.getConnection(database_URL);
             Statement statement =
                     connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery(selectSQLString)
        ) {
            courseList = DAOUtils.resultSetToList(resultSet);
            return courseList;
        }
    }

    public List<String> getScheduleNames() throws SQLException {
        List<String> scheduleNamesList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(database_URL)) {

            DatabaseMetaData databaseMetaData = connection.getMetaData();

            try (ResultSet resultSet =
                         databaseMetaData.getTables(null, null, "%", new String[]{"TABLE"})) {

                while (resultSet.next()) {
                    scheduleNamesList.add(resultSet.getString(3));
                }

                return scheduleNamesList;
            }
        }
    }

    public Course getCourse(int classID) throws SQLException {
        String getCourseSQLString = "SELECT subject_id, course_id_fk as course_id, " +
                "course_title_uq as course_title, class_id, " +
                "seats, code, block, room, teacher " +
                "FROM fall_2015_announcer_classes, " +
                "fall_2015_announcer_courses " +
                "WHERE course_id_pk = course_id_fk AND " +
                "class_id = ?";
        try (Connection connection = DriverManager.getConnection(database_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(getCourseSQLString,
                     ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
        ) {
            preparedStatement.setInt(1, classID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return DAOUtils.resultSetToCourse(resultSet);
            }
        }
    }

    /**
     * Responsible for adding a course to a schedule that the user specifies.
     * @param scheduleName The name of the schedule where the class will be added
     * @return a boolean value indicating whether the class was added successfully or not,
     * true=successfully added, false=not successful
     */
    public boolean addCourse(Course course, String scheduleName) throws SQLException {
        boolean courseLimitReached;
        boolean sameBlock;

        courseLimitReached = checkCourseLimit(scheduleName);

        if (course.getClassID() != 0) {


            sameBlock = checkSameBlock(course, scheduleName);

            if (!courseLimitReached && !sameBlock) {
                insertCourse(course, scheduleName);
                return true;
            } else {
                System.out.print("\nCannot add course. ");

                if (courseLimitReached) {
                    System.out.println("Course limit reached.");
                } else {
                    System.out.println("A course with the same block already exists in the schedule.");
                }

                return false;
            }
        } else {
            System.out.println("\nInvalid class id.");

            return false;
        }
    }

    /**
     * Checks if the course limit for a schedule has already been reached
     * @param scheduleName The name of the schedule to check
     * @return a boolean value indicating whether or not the course limit has been reached,
     * true=limit reached, false=limit not reached
     */
    private boolean checkCourseLimit(String scheduleName) throws SQLException {
        final int COURSE_LIMIT = 7;
        final int LAST_BLOCK = 8;

        boolean courseLimitReached = false;
        int numCourses;

        String courseLimitCheck = "SELECT COUNT(*) FROM \"" + scheduleName + "\" WHERE block <= " + LAST_BLOCK;

        try (Connection connection = DriverManager.getConnection(database_URL);
        Statement statement = connection.createStatement()
        ) {
            try (ResultSet resultSet = statement.executeQuery(courseLimitCheck)) {
                resultSet.next();
                numCourses = resultSet.getInt(1);
                if (numCourses >= COURSE_LIMIT) {
                    courseLimitReached = true;
                }

                return courseLimitReached;
            }
        }
    }

    /**
     * Checks if the course being added has the same block as one of the courses in the schedule
     * @param scheduleName Schedule name that the course is being added to
     * @return a boolean value indicating whether a course with the same block exists in the schedule,
     * true=exists a course with the same block, false=does not exist a course with the same block
     */
    private boolean checkSameBlock(Course course, String scheduleName) throws SQLException {
        boolean sameBlock = false;

        String sameBlockCheck = "SELECT block FROM \"" + scheduleName + "\"";

        try (Connection connection = DriverManager.getConnection(database_URL);
        Statement statement = connection.createStatement()
        ) {
            try (ResultSet resultSet = statement.executeQuery(sameBlockCheck)) {
                while (resultSet.next()) {
                    if (resultSet.getInt(1) == course.getBlock()) {
                        sameBlock = true;
                    }
                }
            }
        }

        return sameBlock;
    }

    /**
     * Responsible for inserting a course into a specified schedule
     * @param scheduleName The schedule for the course to be inserted to
     */
    private void insertCourse(Course course, String scheduleName) throws SQLException {
        String addCourseSQLSTRING = "INSERT INTO \"" + scheduleName + "\" VALUES (" +
                course.getSubjectID() + ", " +
                "'" + course.getCourseID() + "', " +
                "'" + course.getCourseTitle() + "', " +
                course.getClassID() + ", " +
                course.getSeats() + ", " +
                "'" + course.getCode() + "', " +
                course.getBlock() + ", " +
                "'" + course.getRoom() + "', " +
                "'" + course.getTeacher() + "')";

        try (Connection connection = DriverManager.getConnection(database_URL);
        Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(addCourseSQLSTRING);
        }
    }

    /**
     * Responsible for creating a new schedule
     */
    public boolean createSchedule(String scheduleName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_URL);
             Statement statement =
                     connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            String createTableSQLString = "CREATE TABLE \"" + scheduleName + "\" ( " +
                    "subject_id INTEGER NOT NULL, " +
                    "course_id CHAR(8) NOT NULL, " +
                    "course_title VARCHAR(40) NOT NULL, " +
                    "class_id INTEGER NOT NULL, " +
                    "seats INTEGER NOT NULL, " +
                    "code CHAR(1) NOT NULL, " +
                    "block INTEGER NOT NULL, " +
                    "room VARCHAR(15) NOT NULL, " +
                    "teacher VARCHAR(30))";
            statement.execute(createTableSQLString);

            return true; //If no SQLException occurs
        }
    }

    /**
     * Responsible for renaming a schedule
     * @param scheduleName The name of the schedule to be renamed
     */
    public boolean renameSchedule(String scheduleName, String newScheduleName) throws SQLException {

        try (Connection connection = DriverManager.getConnection(database_URL);
             Statement statement =
                     connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            String renameTableSQLString = "RENAME TABLE \"" + scheduleName +
                    "\" TO \"" + newScheduleName + "\"";

            statement.executeUpdate(renameTableSQLString);

            return true; //If no SQLException occurs
        }
    }

    /**
     * Responsible for making a copy of a schedule
     * @param scheduleName The schedule name to be copied
     */
    public boolean duplicateSchedule(String scheduleName, String duplicateScheduleName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(database_URL);
             Statement statement =
                     connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            String createDuplicateTableSQLString = "CREATE TABLE \"" + duplicateScheduleName +
                    "\" AS SELECT * FROM \"" + scheduleName + "\" WITH NO DATA";

            String insertDuplicateTableSQLString = "INSERT INTO \"" + duplicateScheduleName + "\" (subject_id, course_id, course_title, " +
                    "class_id, seats, code, block, room, teacher) " +
                    "SELECT subject_id, course_id, course_title, class_id, seats, code, block, room, teacher " +
                    "FROM \"" + scheduleName + "\"";

            statement.execute(createDuplicateTableSQLString);
            statement.executeUpdate(insertDuplicateTableSQLString);

            return true; //If no SQLException occurs
        }
    }

    /**
     * Responsible for executing the sql query to delete the course from the specified schedule
     * @param classID The classID for the class to be removed
     * @param scheduleName The schedule in which the class will be removed
     * @return A boolean indicating whether or not the class was removed successfully,
     * true=class successfully deleted, false=class could not be deleted
     */
    public boolean deleteCourse(int classID, String scheduleName) throws SQLException {
        int rowsChanged;

        String deleteCourseSQLString = "DELETE  FROM \"" + scheduleName + "\" " +
                "WHERE class_id = ?";

        try (Connection connection = DriverManager.getConnection(database_URL);
             PreparedStatement preparedStatement =
                     connection.prepareStatement(deleteCourseSQLString,
                             ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            preparedStatement.setInt(1, classID);
            rowsChanged = preparedStatement.executeUpdate();

            return rowsChanged != 0;//If true - class successfully deleted, If false - class does not exist
        }
    }

    /**
     * Responsible for deleted a schedule
     * @param scheduleName The name of the schedule to be deleted
     */
    public void deleteSchedule(String scheduleName) throws SQLException {

        try (Connection connection = DriverManager.getConnection(database_URL);
             Statement statement =
                     connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {

            String dropTableSQLString = "DROP TABLE \"" + scheduleName + "\"";

            statement.executeUpdate(dropTableSQLString);

        }
    }

}
