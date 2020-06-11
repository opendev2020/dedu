# Project Title
This project is for the research of secure deduplication, providing relevant test data sets and codes.

# Getting Started
## Prerequisites
* MySQL 5.7+
* JDK 1.8+
* Apache Maven 3.5+

## Directory Structure
dedu
    |   a.properties    // elliptic curve parameters
    |   README.md
    |   run.sh    // test script
    |   secdedu-1.0-SNAPSHOT.jar    // test program
    |   test_sample    // test sample
    |
    +---data
    |       dedu.sql.zip    // test data set
    |
    \\---lib
        import.sh    // third-party library import script
        jpbc-api-2.0.0.jar    // third-party library
        jpbc-plaf-2.0.0.jar    // third-party library
        rabin-1.0.1.jar    // third-party library

## Import Data
1. Create a database in MySQL: dedu
```
create database dedu;
```
2. Create user: dedu, password: cust
```
create user dedu identified by 'cust';
```
3. Assign permissions to users
```
grant all on dedu.* to dedu@localhost;
```
4. Unzip the file dedu.sql.zip in the data directory
5. Import the decompressed file dedu.sql.bak into the database
```
mysql -u dedu -p dedu < dedu.sql.bak
```

## Import Third-party Libraries
Execute the import.sh file in the lib directory to import the third-party libraries.

If you are using Windows OS, you can rename import.sh to import.cmd, and then execute the file at the command line.

## Test
Run run.sh

If you are using Windows OS, you can rename run.sh to run.cmd and then execute the file at the command line.