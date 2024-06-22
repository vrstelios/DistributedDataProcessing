import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class methods {

    public static void pipelinedHashJoin(Connection sqliteConnection, Jedis jedis, String nameTable, Duration expiration) throws Exception {
        long startTime = System.nanoTime();
        // Fetch data from SQLite
        Statement sqliteStmt = sqliteConnection.createStatement();
        ResultSet sqliteRs = sqliteStmt.executeQuery("SELECT user_id, timestamp FROM "+ nameTable +"");

        // Create a hash table for SQLite data
        Map<String, String> sqliteHashTable = new HashMap<>();

        while (sqliteRs.next()) {
            String id = sqliteRs.getString("user_id");
            String timestamp = sqliteRs.getString("timestamp");
            sqliteHashTable.put(id, timestamp);
        }

        long fetchSQLiteTime = System.nanoTime();

        // Perform pipelined hash join
        // Fetch data from Redis in batches using SCAN for efficiency
        String cursor = "0";
        do {
            //διαβάσει τα δεδομένα από τη Redis σε παρτίδες
            ScanResult<String> scanResult = jedis.scan(cursor, new ScanParams().match(nameTable + ":*"));
            List<String> keys = scanResult.getResult();
            cursor = scanResult.getCursor();

            Pipeline pipeline = jedis.pipelined();
            for (String key : keys) {
                pipeline.hgetAll(key);
            }

            List<Object> results = pipeline.syncAndReturnAll();

            for (Object result : results) {
                if (result instanceof Map) {
                    Map<String, String> redisData = (Map<String, String>) result;
                    String id = redisData.get("user_id");
                    String redisTimestampString = redisData.get("timestamp");

                    if (id != null && sqliteHashTable.containsKey(id)) {
                        String sqliteTimestampString = sqliteHashTable.get(id);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime sqliteTimestamp = LocalDateTime.parse(sqliteTimestampString, formatter);
                        LocalDateTime redisTimestamp = LocalDateTime.parse(redisTimestampString, formatter);

                        // Check if the timestamps differ by less than the expiration duration
                        if (Math.abs(Duration.between(sqliteTimestamp, redisTimestamp).toHours()) <= expiration.toHours()) {
                            // Join condition met, process the join
                            System.out.println("Match found for user_id: " + id);
                            //System.out.println("SQLite data: " + sqliteTimestampString);
                            //System.out.println("Redis data: " + redisTimestampString);
                        }
                    }
                }
            }
        } while (!cursor.equals("0"));

        long endTime = System.nanoTime();

        System.out.println("Time taken to fetch SQLite data: " + (fetchSQLiteTime - startTime) / 1_000_000 + " ms");
        System.out.println("Time taken to perform join: " + (endTime - fetchSQLiteTime) / 1_000_000 + " ms");
    }

    public static void semiJoin(Connection sqliteConnection, Jedis jedis, String nameTable) throws Exception{
        long startTime = System.nanoTime();
        // Fetch keys from SQLite
        Statement sqliteStmt = sqliteConnection.createStatement();
        ResultSet sqliteRs = sqliteStmt.executeQuery("SELECT user_id FROM " + nameTable);

        // Create a hash table for SQLite data
        Set<String> sqliteKeys = new HashSet<>();

        while (sqliteRs.next()) {
            String id = sqliteRs.getString("user_id");
            sqliteKeys.add(id);
        }
        long fetchSQLiteTime = System.nanoTime();

        // Fetch data from Redis that matches the keys from SQLite
        Map<String, Map<String, String>> redisDataMap = new HashMap<>();
        for (String id : sqliteKeys) {
            String redisKey = nameTable + ":" + id;
            if (jedis.exists(redisKey)) {
                Map<String, String> redisData = jedis.hgetAll(redisKey);
                redisDataMap.put(id, redisData);
            }
        }

        // Perform semi-join and output the result
        for (String id : sqliteKeys) {
            if (redisDataMap.containsKey(id)) {
                System.out.println("Semi-Join Result: user_id: " + id);
            }
        }
        long endTime = System.nanoTime();

        System.out.println("Time taken to fetch SQLite data: " + (fetchSQLiteTime - startTime) / 1_000_000 + " ms");
        System.out.println("Time taken to perform join: " + (endTime - fetchSQLiteTime) / 1_000_000 + " ms");
    }
}
