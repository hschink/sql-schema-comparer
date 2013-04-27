sql-schema-comparer
===================

**Experimental** library to compare and check SQL schemas and statements

Details
-------

`sql-schema-comparer` allows to compare

1. two SQL schemes with each other,
2. a SQL statement with an SQL schema.

Therfor, `sql-schema-comparer` transforms SQL statements and database definitions into a graph representation. The graph
representation is then used for comparison.

Frontends
---------

`sql-schema-comparer` brings *limited* support for

- SQLite (database schema and SQL statement parsing)
- JPA (annotations)

Limitations
-----------

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
