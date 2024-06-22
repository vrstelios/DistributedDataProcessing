# Distributed Data Processing

Αυτό το project αναπτύσσει ένα πρόγραμμα που γεμίζει με τυχαίες εγγραφές δύο πίνακες σε δύο διαφορετικές βάσεις δεδομένων (SQLite, Redis) και υλοποιεί εξωτερικά αλγόριθμους σύνδεσης δεδομένων, όπως createTable, insertData, pipelined hash join και semi-join.

## Σκοπός

Ο σκοπός αυτής της εργασίας είναι η ανάπτυξη ενός συστήματος που:
- Δημιουργεί και γεμίζει βάσεις δεδομένων με τυχαίες εγγραφές.
- Υλοποιεί αλγόριθμους σύνδεσης δεδομένων pipelined hash join και semi-join.

## Λειτουργίες

Το πρόγραμμα περιλαμβάνει τις εξής λειτουργίες:
1. Δημιουργία και γέμισμα των βάσεων δεδομένων με τυχαίες εγγραφές.
2. Υλοποίηση pipelined hash join και semi-join αλγορίθμων.

## Προαπαιτούμενα

- Java Development Kit (JDK)
- Docker (αν θέλεις να τρέξεις το project με Docker)
- Redis
- SQLite

## Διαμόρφωση

Για να τρέξετε το project, πρέπει να ρυθμίσετε τα στοιχεία των βάσεων δεδομένων στις κατάλληλες μεταβλητές στη `main` κλάση.


```java 
private static final String REDIS_HOST = "localhost";
private static final int REDIS_PORT = 6379;
private static final String SQLITE_URL = "jdbc:sqlite:sample.db";
```
 

## Οδηγίες Εκτέλεσης

## Χωρίς Docker
1. Κλωνοποιήστε το repository: `git clone https://github.com/yourusername/distributed-data-processing.git`
`cd distributed-data-processing`
2. Ρυθμίστε τα στοιχεία των βάσεων δεδομένων στη main.java.
3. Εκτελέστε την `main.java` μέσω του IDE σας ή από τη γραμμή εντολών:
`javac Main.java`
`java Main`

## Με Docker
1. Κλωνοποιήστε το repository: `git clone https://github.com/yourusername/distributed-data-processing.git`
`cd distributed-data-processing`
2. Εκτελέστε την παρακάτω εντολή για να κατασκευάσετε και να τρέξετε το Docker container: `docker-compose up --build`

## Αλγόριθμοι
### Pipelined Hash Join
Ο αλγόριθμος Pipelined Hash Join χρησιμοποιείται για τη σύνδεση δύο συνόλων δεδομένων που αποθηκεύονται στις δύο βάσεις δεδομένων (SQLite και Redis).

### Semi-Join
Ο αλγόριθμος Semi-Join χρησιμοποιείται για τη σύνδεση δύο συνόλων δεδομένων με έναν τρόπο που φιλτράρει τα δεδομένα του ενός συνόλου με βάση τα δεδομένα του άλλου.
