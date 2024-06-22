import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;

public class main {

    private static final String REDIS_HOST = "192.168.2.2";
    private static final int REDIS_PORT = 6379;
    private static final String SQLITE_URL = "jdbc:sqlite:identifier.sqlite";

    public static void main(String[] args) throws Exception {
        try {
            // Connect to databases.
            Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);
            System.out.println("Connected to Redis database.");
            jedis.flushDB();

            Connection sqliteConnection = DriverManager.getConnection(SQLITE_URL);
            System.out.println("Connected to SQLite database.");

            // call function for create, insert, pipelined hash join and  semi-join
            databases.sqlitedb.createSQLiteTable(sqliteConnection, "dataset1K");
            //databases.sqlitedb.createSQLiteTable(sqliteConnection, "dataset100K");
            //databases.sqlitedb.createSQLiteTable(sqliteConnection, "dataset250K");
            //databases.sqlitedb.createSQLiteTable(sqliteConnection, "dataset500K");
            //databases.sqlitedb.createSQLiteTable(sqliteConnection, "dataset1000K");

            databases.sqlitedb.insertSQLiteData(sqliteConnection, "dataset1K", 1000);
            //databases.sqlitedb.insertSQLiteData(sqliteConnection, "dataset100K", 100_000);
            //databases.sqlitedb.insertSQLiteData(sqliteConnection, "dataset250K", 250_000);
            //databases.sqlitedb.insertSQLiteData(sqliteConnection, "dataset500K", 500_000);
            //databases.sqlitedb.insertSQLiteData(sqliteConnection, "dataset1000K", 1_000_000);

            databases.redisdb.insertRedisData(jedis, "dataset1K", 1000);
            //databases.redisdb.insertRedisData(jedis, "dataset100K", 100_000);
            //databases.redisdb.insertRedisData(jedis, "dataset250K", 250_000);
            //databases.redisdb.insertRedisData(jedis, "dataset500K", 500_000);
            //databases.redisdb.insertRedisData(jedis, "dataset1000K", 1_000_000);

            methods.pipelinedHashJoin(sqliteConnection, jedis, "dataset1K", Duration.ofHours(672));
            //methods.pipelinedHashJoin(sqliteConnection, jedis, "dataset100K", Duration.ofHours(1000));
            //methods.pipelinedHashJoin(sqliteConnection, jedis, "dataset250K", Duration.ofHours(1000));
            //methods.pipelinedHashJoin(sqliteConnection, jedis, "dataset500K", Duration.ofHours(1000));
            //methods.pipelinedHashJoin(sqliteConnection, jedis, "dataset1000K", Duration.ofHours(1000));


            methods.semiJoin(sqliteConnection, jedis, "dataset1K");
            //methods.semiJoin(sqliteConnection, jedis, "dataset100K");
            //methods.semiJoin(sqliteConnection, jedis, "dataset250K");
            //methods.semiJoin(sqliteConnection, jedis, "dataset500K");
            //methods.semiJoin(sqliteConnection, jedis, "dataset1000K");

            //databases.redisdb.displayRedisData(jedis);

            // Close connections
            jedis.close();
            sqliteConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
