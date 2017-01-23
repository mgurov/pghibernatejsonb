## Running with maven
 
 `mvn verify` should start a `db` docker container with postgres 9.5 and after some delay 
  connect to it yielding the following:
   
````
Tests in error:
  LogDaoHibernateIT>LogDaoIT.insertion:19 » Persistence org.hibernate.exception....
  LogDaoHibernateIT>LogDaoIT.saveAndRead:33 » Persistence org.hibernate.exceptio...
````

in particular: 

````dtd
Caused by: org.postgresql.util.PSQLException: Unsupported Types value: 1.029.991.479
	at org.postgresql.jdbc.PgPreparedStatement.setObject(PgPreparedStatement.java:753)
	at org.postgresql.jdbc.PgPreparedStatement.setObject(PgPreparedStatement.java:987)

````

where this value is set by hibernate at [JdbcTypeJavaClassMappings.java line 54](https://github.com/hibernate/hibernate-orm/blob/master/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/JdbcTypeJavaClassMappings.java#L54) where the jdbc type is taken from `java.lang.Object::hashCode()`.

## Running individual test (IDE) against a running postgres instance

Set the following system properties, with the defaults mentioned: 
`````
-Dpostgres_host=localhost -Dpostgres_port=5432 -Dpostgres_user=postgres -Dpostgres_password= -Dpostgres_db=postgres
`````

# Credits

  Adapted from [fabric8's Docker Maven Plugin Shootout](https://github.com/fabric8io/shootout-docker-maven)