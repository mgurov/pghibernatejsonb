package com.github.mgurov.jdbcplayground;

import org.flywaydb.core.Flyway;
import org.postgresql.jdbc2.optional.SimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class ConnectionManager {

    public static String getConnectionUrl() {
        Map<String, String> env = System.getenv();
        String port = System.getProperty("postgres_port", env.getOrDefault("POSTGRES_PORT", "5432"));
        String host = System.getProperty("postgres_host", env.getOrDefault("POSTGRES_HOST", "localhost"));
        String db = System.getProperty("postgres_db", env.getOrDefault("POSTGRES_DB", "postgres"));
        return String.format("jdbc:postgresql://%s:%d/%s", host, Integer.valueOf(port), db);
    }

    public static String getUser() {
        return System.getProperty("postgres_user", System.getenv().getOrDefault("POSTGRES_USER", "postgres"));
    }

    public static String getPassword() {
        return System.getProperty("postgres_password", System.getenv().get("POSTGRES_PASSWORD"));
    }

    // ensures db migrations has been applied before returning the connection url
    public static String getConnectionUrlPreparedDatabase() {
        return makeDatasource().getUrl();
    }

    public static SimpleDataSource makeDatasource() {
        SimpleDataSource ds = new SimpleDataSource();
        ds.setUrl(getConnectionUrl());
        ds.setUser(getUser());
        ds.setPassword(getPassword());

        applyDbMigrations(ds);

        return ds;
    }

    public static synchronized void applyDbMigrations(DataSource dataSource) {
        waitForConnectionAvailability(dataSource);
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }

    private static void waitForConnectionAvailability(DataSource dataSource) {
        for (int i = 0; i < 100; i++) {
            try {
                try (Connection c = dataSource.getConnection()) {
                    System.out.println("Succesfully obtained db connection");
                    return;
                }
            } catch (SQLException e) {
                System.out.printf("retry %d establishing connection: %s\n", i + 1, e.getMessage());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    throw Throwables.propagate(e);
                }
            }
        }
        throw new IllegalStateException("Could not establish connection with the database");
    }
}
