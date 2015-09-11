package com.rjew.ArenaScheduler;

import java.sql.*;
import java.util.List;

/**
 * Responsible for access to a database. Executes queries to the database.
 * Take inputs, uses those inputs to access database, and returns some value from the database
 * Not concerned with collecting user input or output.
 * Accepts parameters, executes queries with those parameters and returns a result.
 */
class DAOManager {
    private final String database_URL;

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

}
