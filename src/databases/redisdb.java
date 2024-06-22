package databases;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Set;

public class redisdb {

    public static void insertRedisData(Jedis jedis, String tableName, int numRows) {
        // Clear existing data with the specified name
        jedis.del(tableName);

        // Initialize a pipeline for batch operations
        Pipeline pipe = jedis.pipelined();
        // Define batch size for pipelining
        int batchSize = 10_000;
        int min = 10000;
        int max = 99999;

        Random random = new Random();

        // Loop to insert data into Redis
        long startTime = System.nanoTime();
        for (int i = 0; i < numRows; i++) {
            String userId = String.valueOf(random.nextInt(max - min) + min);
            //String userId = String.valueOf(10000 + i);
            String timestamp = LocalDateTime.now().minusYears(random.nextInt(10)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Add columns to the hash set
            pipe.hset(tableName + ":" + userId, "user_id", userId);
            pipe.hset(tableName + ":" + userId, "timestamp", timestamp);

            // Execute the pipeline when the batch size is reached
            if ((i + 1) % batchSize == 0) {
                pipe.sync();
            }
        }
        // Execute any remaining pipeline operations
        pipe.sync();
        long endTime = System.nanoTime();
        System.out.printf("SQLite: Function insertData for table %s took %.4f seconds%n", tableName, (endTime - startTime) / 1e9);
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
