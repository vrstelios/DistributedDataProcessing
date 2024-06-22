package databases;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class sqlitedb {

    private static final Random random = new Random();


    public static void createSQLiteTable(Connection conn, String tableName) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            //create a table in SQLite database
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (counter INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, timestamp TEXT)";
            stmt.execute(sql);
        }
    }

    // Εισάγωση δεδομένων στον πίνακα SQLite
    public static void insertSQLiteData(Connection conn, String tableName, int numRows) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (user_id, timestamp) VALUES (?, ?)";
        // Batch size for inserting data
        int batchSize = 10_000;
        List<Object[]> batch = new ArrayList<>();
        int min = 10000;
        int max = 99999;
        Random random = new Random();

        // Record start time for performance measurement
        long startTime = System.nanoTime();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Loop to generate and insert data into SQLite
            for (int i = 0; i < numRows; i++) {
                int userId = random.nextInt(max - min) + min;
                //int userId = 10000 + i;
                String timestamp = LocalDateTime.now().minusYears(random.nextInt(10)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                batch.add(new Object[]{userId, timestamp});
                // Execute the batch when it reaches the specified size
                if (batch.size() == batchSize) {
                    executeBatch(pstmt, batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                executeBatch(pstmt, batch);
            }
        }
        long endTime = System.nanoTime();
        System.out.printf("SQLite: Function insertData for table %s took %.4f seconds%n", tableName, (endTime - startTime) / 1e9);
    }
    // Method to execute a batch insert in SQLite
    private static void executeBatch(PreparedStatement pstmt, List<Object[]> batch) throws SQLException {
        // Iterate through the batch and add data to the prepared statement
        for (Object[] data : batch) {
            pstmt.setInt(1, (int) data[0]);
            pstmt.setString(2, (String) data[1]);
            pstmt.addBatch();
        }
        pstmt.executeBatch();
    }
}
