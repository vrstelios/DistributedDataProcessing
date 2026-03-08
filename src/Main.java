import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        try {
            // Connect to Redis databases.
            Jedis jedis = new Jedis(DatabaseConfig.REDIS_HOST, DatabaseConfig.REDIS_PORT);
            System.out.println("Connected to Redis database.");
            jedis.flushDB();

            // Connect to SQLite databases.
            Connection sqliteConnection = DriverManager.getConnection(DatabaseConfig.SQLITE_URL);
            System.out.println("Connected to SQLite database.");

            // call function for create, insert, pipelined hash join and  semi-join
            databases.SQLiteManager.createSQLiteTable(sqliteConnection, "dataset1K");
            databases.SQLiteManager.createSQLiteTable(sqliteConnection, "dataset100K");
            databases.SQLiteManager.createSQLiteTable(sqliteConnection, "dataset250K");
            databases.SQLiteManager.createSQLiteTable(sqliteConnection, "dataset500K");
            databases.SQLiteManager.createSQLiteTable(sqliteConnection, "dataset1000K");

            databases.SQLiteManager.insertSQLiteData(sqliteConnection, "dataset1K", 1000);
            databases.SQLiteManager.insertSQLiteData(sqliteConnection, "dataset100K", 100_000);
            databases.SQLiteManager.insertSQLiteData(sqliteConnection, "dataset250K", 250_000);
            databases.SQLiteManager.insertSQLiteData(sqliteConnection, "dataset500K", 500_000);
            databases.SQLiteManager.insertSQLiteData(sqliteConnection, "dataset1000K", 1_000_000);

            databases.RedisManager.insertRedisData(jedis, "dataset1K", 1000);
            databases.RedisManager.insertRedisData(jedis, "dataset100K", 100_000);
            databases.RedisManager.insertRedisData(jedis, "dataset250K", 250_000);
            databases.RedisManager.insertRedisData(jedis, "dataset500K", 500_000);
            databases.RedisManager.insertRedisData(jedis, "dataset1000K", 1_000_000);

            methods.pipelinedHashJoin(sqliteConnection, jedis, "dataset1K", Duration.ofHours(672));
            methods.pipelinedHashJoin(sqliteConnection, jedis, "dataset100K", Duration.ofHours(1000));
            methods.pipelinedHashJoin(sqliteConnection, jedis, "dataset250K", Duration.ofHours(1000));
            methods.pipelinedHashJoin(sqliteConnection, jedis, "dataset500K", Duration.ofHours(1000));
            methods.pipelinedHashJoin(sqliteConnection, jedis, "dataset1000K", Duration.ofHours(1000));

            methods.semiJoin(sqliteConnection, jedis, "dataset1K");
            methods.semiJoin(sqliteConnection, jedis, "dataset100K");
            methods.semiJoin(sqliteConnection, jedis, "dataset250K");
            methods.semiJoin(sqliteConnection, jedis, "dataset500K");
            methods.semiJoin(sqliteConnection, jedis, "dataset1000K");

            //databases.redisdb.displayRedisData(jedis);

            // Close connections
            jedis.close();
            sqliteConnection.close();
        } catch (Exception e) {
            System.err.println("Critical Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
