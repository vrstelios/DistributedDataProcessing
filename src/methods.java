import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.*;

public class methods {

    public static void pipelinedHashJoin(Connection sqliteConnection, Jedis jedis, String tableName, Duration expiration) throws Exception {
        long startTime = System.currentTimeMillis();
        long maxDiffMillis = expiration.toMillis();

        // Use List for support multiply record with same user.
        Map<Integer, List<Long>> sqliteHashTable = new HashMap<>();

        // Fetch data from SQLite
        String query = "SELECT user_id, timestamp FROM " + tableName;
        try (Statement stmt = sqliteConnection.createStatement(); ResultSet rs= stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("user_id");
                long ts = rs.getLong("timestamp");

                sqliteHashTable.computeIfAbsent(id, k -> new ArrayList<>()).add(ts);
            }
        }

        long fetchSQLiteTime = System.currentTimeMillis();
        int matchCount = 0;


        // Fetch data from Redis in batches using keys
        Set<String> allKeys = jedis.smembers(tableName + ":keys");

        // Perform pipelined hash join
        List<String> keyList = new ArrayList<>(allKeys);
        int batchSize = 1000;

        for (int i = 0; i < keyList.size(); i += batchSize) {
            int end = Math.min(i + batchSize, keyList.size());
            List<String> batchKeys = keyList.subList(i, end);

            try (Pipeline pipe = jedis.pipelined()) {
                for (String key : batchKeys) {
                    pipe.hgetAll(key);
                }
                List<Object> results = pipe.syncAndReturnAll();

                for (Object res : results) {
                    Map<String, String> redisData = (Map<String, String>) res;
                    int rId = Integer.parseInt(redisData.get("user_id"));
                    long rTs = Long.parseLong(redisData.get("timestamp"));

                    // Check join Condition
                    if (sqliteHashTable.containsKey(rId)) {
                        for (long sTs : sqliteHashTable.get(rId)) {
                            // Check timestamp (Condition)
                            if (Math.abs(rTs - sTs) <= maxDiffMillis) {
                                matchCount++;
                                // System.out.println("Match! User: " + rId);
                            }
                        }
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("--- Join Results for " + tableName + " ---");
        System.out.println("Total Matches Found: " + matchCount);
        System.out.println("Build Phase (SQLite): " + (fetchSQLiteTime - startTime) + " ms");
        System.out.println("Probe Phase (Redis): " + (endTime - fetchSQLiteTime) + " ms");
        System.out.println("Total Time: " + (endTime - startTime) + " ms");
    }


    public static void semiJoin(Connection sqliteConnection, Jedis jedis, String tableName) throws Exception{
        long startTime = System.currentTimeMillis();

        // Fetch Ids from SQLite
        Set<Integer> sqliteIds = new HashSet<>();
        String query = "SELECT DISTINCT user_id FROM " + tableName;

        try (Statement stmt = sqliteConnection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                sqliteIds.add(rs.getInt("user_id"));
            }
        }

        long fetchSQLiteTime = System.currentTimeMillis();
        int matchCount = 0;

        // Check  Redis only for Ids
        List<Integer> idList = new ArrayList<>(sqliteIds);
        int batchSize = 1000;

        for (int i = 0; i < idList.size(); i += batchSize) {
            int end = Math.min(i + batchSize, idList.size());
            List<Integer> batch = idList.subList(i, end);

            try (Pipeline pipe = jedis.pipelined()) {
                for (Integer id : batch) {
                    pipe.exists(tableName + ":" + id);
                }

                List<Object> results = pipe.syncAndReturnAll();
                for (Object res : results) {
                    if ((Boolean) res) {
                        matchCount++;
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("--- Semi-Join Results for " + tableName + " ---");
        System.out.println("Distinct SQLite IDs: " + sqliteIds.size());
        System.out.println("Matches found in Redis: " + matchCount);
        System.out.println("SQLite Extraction: " + (fetchSQLiteTime - startTime) + " ms");
        System.out.println("Redis Probing (Pipeline): " + (endTime - fetchSQLiteTime) + " ms");
    }
}
