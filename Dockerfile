FROM openjdk:11

# set the working directory
WORKDIR /app

# Copying to the container
COPY src/ /app
COPY src/libs /app/libs

# Build project
RUN javac -cp ".:/app/libs/*" main.java databases/redisdb.java databases/sqlitedb.java methods.java


# define the run command
CMD ["java", "-cp", ". /src", "main"]
