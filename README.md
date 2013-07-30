# sql-schema-comparer #

[![Build Status](https://travis-ci.org/hschink/sql-schema-comparer.png?branch=master)](https://travis-ci.org/hschink/sql-schema-comparer)

**Experimental** library to compare and check SQL schemas and statements

## Details ##

`sql-schema-comparer` allows to compare

1. two SQL schemes with each other,
2. a SQL statement with an SQL schema.

Therfor, `sql-schema-comparer` transforms SQL statements and database definitions into a graph representation. The graph
representation is then used for comparison.

## Frontends ##

`sql-schema-comparer` brings *limited* support for

- SQLite (database schema and SQL statement parsing)
- JPA (annotations)

## Limitations ##

Two SQL schemes (databases) must only differ by one of the following transformations:

- **Table**
 - Create
 - Drop
 - Rename
- **Column**
 - Create
 - Drop
 - Rename
 - Move
- **Column Constraint**
 - Create
 - Drop
- **Foreign Key Relation**
 - Create
 - Drop

## Usage ##

Please refer to the [unit tests] [ut] to get a detailed description of how to use the library in your code.

[ut]: https://github.com/hschink/sql-schema-comparer/tree/master/test

## Development ##

If you like to join sql-schema-comparer development you may just want to use [Eclipse IDE][eclipse].
Just use [gradle's Eclipse plug-in][eclipse plug-in] with ``gradle eclipse`` to create the necessary Eclipse
configuration files.

Happy programming! :smile:

[eclipse]: [http://www.eclipse.org/]
[eclipse plug-in]: [http://www.gradle.org/docs/current/userguide/eclipse_plugin.html]

## Stand-alone JAR ##

You create a stand-alone JAR with [gradle][]: ``gradle standaloneJar``. You'll find the JAR in ``build/libs``.

[gradle]: http://www.gradle.org

## Commandline Usage ##

The following examples assume that you created a [stand-alone JAR](#stand-alone-jar).

**Compare two SQLite database files**

`> java -jar sql-schema-comparer.jar file1.sqlite file2.sqlite`

**Compare an SQLite database file with an SQL statement**

`> java -jar sql-schema-comparer.jar -statement $STATEMENT file.sqlite`
