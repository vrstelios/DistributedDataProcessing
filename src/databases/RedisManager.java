package databases;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RedisManager {

    private static final Random random = new Random();

    public static void insertRedisData(Jedis jedis, String tableName, int numRows) {
        // Clear existing data with the specified name
        //jedis.del(tableName);

        // Define batch size for pipelining
        int batchSize = 10_000;
        int minUserId = 10000;
        int maxUserId = 99999;
        long tenYearsInMs = 10L * 365 * 24 * 60 * 60 * 1000;

        long startTime = System.currentTimeMillis();

        // Initialize a pipeline for batch operations
        try (Pipeline pipe = jedis.pipelined()) {
            // Loop to insert data into Redis
            for (int i = 0; i < numRows; i++) {
                int userId = random.nextInt(maxUserId - minUserId + 1) + minUserId;
                long randomTimestamp = System.currentTimeMillis() - (long)(random.nextDouble() * tenYearsInMs);

                String key = tableName + ":" + i;

                // Create Map for mass insert in Hash
                Map<String, String> data = new HashMap<>();
                data.put("user_id", String.valueOf(userId));
                data.put("timestamp", String.valueOf(randomTimestamp));

                pipe.hmset(key, data);

                // Add columns to the hash set
                pipe.sadd(tableName + ":keys", key);

                // Execute the pipeline when the batch size is reached
                if ((i + 1) % batchSize == 0) {
                    pipe.sync();
                }
            }
            // Execute any remaining pipeline operations
            pipe.sync();
        }

        long endTime = System.currentTimeMillis();
        System.out.printf("Redis: Dataset %s | Rows: %d | Time: %.3f s%n", tableName, numRows, (endTime - startTime) / 1000.0);
    }

    public static void displayRedisData(Jedis jedis) {
        // Retrieve all keys from Redis
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            System.out.println("Key: " + key);
            System.out.println("Value: " + jedis.hgetAll(key));
        }
    }
}
