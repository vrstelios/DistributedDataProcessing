# Distributed Data Processing

An advanced Java application for data processing in a distributed environment, using SQLite and Redis. The application implements optimized join algorithms outside the database engines.

---

## Technologies & Architecture

- Java 17: Core logic.
- SQLite: Relational database for storing structured data.
- Redis: NoSQL in-memory key-value store for high-speed operations.
- Jedis & Pipelining: Used to minimize network latency in Redis.
- Docker & Docker Compose: For full isolation and easy deployment/execution.

---

# Experiment Results (Benchmarks)

The following results were obtained by executing the algorithms on
different dataset sizes (from **1K to 1M records**) with a **join time
window of 672 hours**.

| Dataset Size | Algorithm   | SQLite Fetch | Join Execution | Total Time |
|--------------|------------|--------------|----------------|------------|
| 1K           | Hash Join  | 11 ms        | 38 ms          | 49 ms      |
| 1K           | Semi-Join  | 2 ms         | 64 ms          | 66 ms      |
| 100K         | Hash Join  | 272 ms       | 1091 ms        | 1363 ms    |
| 100K         | Semi-Join  | 183 ms       | 7055 ms        | 7238 ms    |
| 250K         | Hash Join  | 329 ms       | 1464 ms        | 1793 ms    |
| 250K         | Semi-Join  | 209 ms       | 8162 ms        | 8371 ms    |
| 500K         | Hash Join  | 682 ms       | 1626 ms        | 2308 ms    |
| 500K         | Semi-Join  | 460 ms       | 9468 ms        | 9928 ms    |
| 1000K        | Hash Join  | 1188 ms      | 1529 ms        | 2717 ms    |
| 1000K        | Semi-Join  | 814 ms       | 9430 ms        | 10244 ms   |

---

# Configuration
The database settings are located in the `DatabaseConfig.java class`.
```java 
private static final String REDIS_HOST = "localhost";
private static final int REDIS_PORT = 6379;
private static final String SQLITE_URL = "jdbc:sqlite:sample.db";
```

---

## Execution instructions

## Without Docker
1. Clone the repository: `git clone https://github.com/yourusername/distributed-data-processing.git`
`cd distributed-data-processing`
2. Configure the database data in the `main.java`.
3. Run the `main.java` via your IDE or from the command line:
`javac Main.java`
`java Main`

## With Docker
1. Clone the repository: `git clone https://github.com/yourusername/distributed-data-processing.git`
`cd distributed-data-processing`
2. Run the following command to build and run the Docker container: `docker-compose up --build`

---

## Algorithms
### Pipelined Hash Join
The algorithm Pipelined Hash Join used for the connection two database which saved the two databases (SQLite και Redis).

### Semi-Join
The Semi-Join algorithm is used to join two data sets in a way that filters the data of one set based on the data of the other.

---

### Author
[DoctorVerRossi](https://github.com/vrstelios)

---

If you find this project helpful, please give it a star on GitHub!