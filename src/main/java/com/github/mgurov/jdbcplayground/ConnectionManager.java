package com.github.mgurov.jdbcplayground;

import org.flywaydb.core.Flyway;
import org.postgresql.jdbc2.optional.SimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionManager {

    public static final String PGUSER = "postgres";

    // Extract connection URL from environment variable as setup by docker (or manually)
    public static String getConnectionUrl() {
        String dbPort = System.getenv("DB_PORT");

        // Fallback for alexec Plugin which does not support configuration of link aliases
        if (dbPort == null) {
            dbPort = System.getenv("SHOOTOUT_DOCKER_MAVEN_DB_PORT");
        }
        if (dbPort == null) {
            throw new IllegalArgumentException("No DB_PORT or SHOOTOUT_DOCKER_MAVEN_DB_PORT environment variable set. Please check you docker link parameters.");
        }
        Pattern pattern = Pattern.compile("^[^/]*//(.*)");
        Matcher matcher = pattern.matcher(dbPort);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid format of DB_PORT variable: Expected tcp://host:port and not " + dbPort);
        }
        String hostAndPort = matcher.group(1);
        return "jdbc:postgresql://" + hostAndPort + "/postgres";
    }

    // ensures db migrations has been applied before returning the connection url
    public static String getConnectionUrlPreparedDatabase() {
        return makeDatasource().getUrl();
    }

    public static SimpleDataSource makeDatasource() {
        SimpleDataSource ds = new SimpleDataSource();
        ds.setUrl(getConnectionUrl());
        ds.setUser(PGUSER);

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
                System.out.printf("Failed to obtain connection: %s, retrying %d\n", e.getMessage(), i + 1);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    throw Throwables.propagate(e);
                }
            }
        }
    }
}
