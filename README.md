# Distributed Data Processing

This project develops a program that populates with random records two tables in two different databases (SQLite, Redis) and implements external data join algorithms such as createTable, insertData, pipelined hash join and semi-join.

## Purpose

The purpose of this project is to develop a system that:
- Creates and populates databases with random records.
- Implements pipelined hash join and semi-join algorithms.

## Functions

This program include the following functions:
1. Create and populates the database with random records.
2. Implement pipelined hash join and semi-jion algorithm.

## Prerequisites

- Java Development Kit (JDK)
- Docker (αν θέλεις να τρέξεις το project με Docker)
- Redis
- SQLite

## Configuration

To run the project, you need to set the database elements to the appropriate variables in the `main` class.


```java 
private static final String REDIS_HOST = "localhost";
private static final int REDIS_PORT = 6379;
private static final String SQLITE_URL = "jdbc:sqlite:sample.db";
```
 

## Execution instructions

## Without Docker
1. Clone the repository: `git clone https://github.com/yourusername/distributed-data-processing.git`
`cd distributed-data-processing`
2. Configure the database data in the `main.java`.
3. Run the `main.java` via your IDE or from the command line:
`javac Main.java`
`java Main`

## Με Docker
1. Clone the repository: `git clone https://github.com/yourusername/distributed-data-processing.git`
`cd distributed-data-processing`
2. Run the following command to build and run the Docker container: `docker-compose up --build`

## Αλγόριθμοι
### Pipelined Hash Join
The algorithm Pipelined Hash Join used for the connection two database which saved the two databases (SQLite και Redis).

### Semi-Join
The Semi-Join algorithm is used to join two data sets in a way that filters the data of one set based on the data of the other.
