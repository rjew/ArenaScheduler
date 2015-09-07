package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for access to a database. Executes queries to the database.
 * Take inputs, uses those inputs to access database, and returns some value from the database
 * or a boolean value indicating success.
 * Not concerned with collecting user input or output.
 * Accepts parameters, executes queries with those parameters and returns a result.
 */
public class DAOManager {
    private String database_URL;

    /**
     * Initializes the database_URL
     * @param database_URL The String which is assigned to the database_URL field
     */
    public DAOManager(String database_URL) {
        this.database_URL = database_URL;
    }

    /**
     * Executes the search catalog query
     * @param sqlStmt The SQL statement to be executed
     * @param preparedStatementParameters The parameters to be inserted into the SQL statement
     * @return A List holding the search catalog query results
     * @throws SQLException
     */
    public List<Course> executeSearchCatalogQuery(String sqlStmt, List<Object> preparedStatementParameters)
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

    /**
     * Executes a select query
     * @param selectSQLString The SQL select query to be executed
     * @return A List holding the SQL select query results
     * @throws SQLException
     */
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

    /**
     * Returns the custom schedule names from the database
     * @return A List holding the custom schedule names
     * @throws SQLException
     */
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

    /**
     * Returns a Course object with the specified classID
     * @param classID The classID of the course to be returned
     * @return A Course object with the specified classID
     * @throws SQLException
     */
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
     * @param course The course to be added to the schedule
     * @param scheduleName The name of the schedule where the class will be added
     * @return a boolean value indicating whether the class was added successfully or not,
     * true=successfully added, false=not successful
     * @throws SQLException
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
     * @throws SQLException
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
     * @param course The course that is being added
     * @param scheduleName Schedule name that the course is being added to
     * @return a boolean value indicating whether a course with the same block exists in the schedule,
     * true=exists a course with the same block, false=does not exist a course with the same block
     * @throws SQLException
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
     * @param course The course to be added
     * @param scheduleName The schedule for the course to be inserted to
     * @throws SQLException
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
     * @param scheduleName The name of the schedule to be created
     * @return A boolean value indicating whether the schedule was created or not
     * true=schedule was successfully created with no exceptions thrown, false=schedule could not be created
     * @throws SQLException
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
     * @param newScheduleName The new schedule name
     * @return A boolean value indicating whether the class was successfully renamed
     * true=schedule was successfully renamed with no exceptions thrown, false=schedule could not be renamed
     * @throws SQLException
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
     * @param duplicateScheduleName The schedule name of the duplicate
     * @return A boolean value indicating whether the class was successfully copied
     * true=schedule was successfully duplicated with no exceptions thrown, false=schedule could not be duplicated
     * @throws SQLException
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
     * Deletes a course from a schedule
     * @param classID The classID of the course to be deleted
     * @param scheduleName The schedule from which the course will be deleted
     * @return A boolean value indicating whether the class could be deleted
     * true=class was deleted, false=class could not be deleted
     * @throws SQLException
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
     * Responsible for deleting a schedule
     * @param scheduleName The name of the schedule to be deleted
     * @throws SQLException
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
