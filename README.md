zith-toolkit-dao
====

A collection of utilities for creating DAOs (Data Access Objects).

Features
----

 - A DDL-like language to define beans representing rows from SQL.
 
   You can just copy DDLs with few changes to define a record type. It will generate a Java bean class for each record
   type. A bean class provides a default constructor, a copy constructor, `toString()`, `equals()`, `hashCode()`, and
   many other facilities which help reflect on record objects, generate SQL queries, map values with JDBC.

 - Ready-to-use CRUD DAO and RowMapper implemented with Spring JdbcTemplate.

   Further integration is provided for PostgreSql, such as insertion with generated serial primary key, selection with
   with different locking modes and waiting strategies.

FAQ
----

### MyBatis is enough. Why reinvent the wheel?

It's somehow similar to MyBatis, which also maps query parameters and results to Java beans and adopts the concept of
type handler.

However, MyBatis doesn't provide rich bean classes and doesn't help generate SQL queries.

MyBatis manages sessions and caches itself. Its scope somehow overlaps with the scope of Spring JDBC and Spring
Transaction.

### Since JPA (Hibernate, EclipseLink...) is so mature, there is no needs to use SQL directly.

ORM is not always necessary. And taming an ORM framework is always not easy. ORMs are complex systems, which manage
identities of memory objects, try to distinguish identities from values in a database, ensure values from multiple
read operations get linked to the same memory object whenever the values are from the same imaginary identity, hides
versions and concurrency problems. All these complexities make ORM a leaky abstraction. It's happy when there is
nothing going wrong. But it would cost days if it works in an unexpected way. You may find it too complex comparing to
your own simple business logic when debugging. ORM also prevents utilization of extensions to common SQL. An ORM is too
complex to write an extension to it.

The central concept of an ORM is persistent objects, not relations. Many operations that are meaningful to relations
are hard to be explained as operations to objects. Generally, objects are not friendly to transformations that ignore
identities. Many databases provides a lot of really good wheels for computing values. For
business concerns more about values, using a value-centric schema and leveraging SQL database features may save a lot of
time.