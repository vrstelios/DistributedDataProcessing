package databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class SQLiteManager {

    public static void createSQLiteTable(Connection conn, String tableName) throws SQLException {
        //create a table in SQLite database
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "counter INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "timestamp BIGINT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table " + tableName + " is ready.");
        }
    }

    // Insert data in tables SQLite
    public static void insertSQLiteData(Connection conn, String tableName, int numRows) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (user_id, timestamp) VALUES (?, ?)";
        // Batch size for inserting data
        int batchSize = 10_000;

        // Limit for userId
        int minUserId = 10000;
        int maxUserId = 99999;
        Random random = new Random();

        // Record start-time for performance measurement
        long startTime = System.currentTimeMillis();

        // Disable auto-commit for big speed
        boolean previousAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Loop to generate and insert data into SQLite
            for (int i = 1; i <= numRows; i++) {
                int userId = random.nextInt(maxUserId - minUserId) + minUserId;

                // Random Timestamp for last 10 years in milliseconds
                long tenYearsInMs = 10L * 365 * 24 * 60 * 60 * 1000;
                long randomTimestamp = System.currentTimeMillis() - (long)(random.nextDouble() * tenYearsInMs);

                pstmt.setInt(1, userId);
                pstmt.setLong(2, randomTimestamp);
                pstmt.addBatch();

                // Run batch per 10000 rows
                if (i % batchSize == 0) {
                    pstmt.executeBatch();
                }
            }
            // Run rest rows
            pstmt.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(previousAutoCommit);
        }

        long endTime = System.currentTimeMillis();
        System.out.printf("SQLite: Table %s | Rows: %d | Time: %.3f s%n", tableName, numRows, (endTime - startTime) / 1000.0);
    }
}
