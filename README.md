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

### Eclipse Plug-In ###

If you want to know how `sql-schema-comparer` works within an IDE, have a look on the
[sql-schema-comparer eclipse plug-in][sscp].

[sscp]: https://github.com/hschink/sql-schema-comparer-eclipse-plugin

### Commandline Usage ###

The following examples assume that you created a [stand-alone JAR](#stand-alone-jar).

**Compare two SQLite database files**

`> java -jar sql-schema-comparer-standalone.jar file1.sqlite file2.sqlite`

**Compare an SQLite database file with an SQL statement**

`> java -jar sql-schema-comparer-standalone.jar -statement $STATEMENT file.sqlite`

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

## License ##

!["GNU LGPLv3"](http://www.gnu.org/graphics/lgplv3-88x31.png)
**Apart from the [SqlStatementFrontend.java](src/main/java/org/iti/sqlSchemaComparison/frontends/SqlStatementFrontend.java)**
the sql-schema-comparer is released under the terms of the [LGPL][lgpl].

!["GNU GPLv3"](http://www.gnu.org/graphics/gplv3-88x31.png)
Only the [SqlStatementFrontend.java](src/main/java/org/iti/sqlSchemaComparison/frontends/SqlStatementFrontend.java) is
released under the terms of the [GPL][gpl].

[lgpl]: http://www.gnu.org/licenses/lgpl-3.0.en.html
[gpl]: http://www.gnu.org/licenses/gpl-3.0.en.html
