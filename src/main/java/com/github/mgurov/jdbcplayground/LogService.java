package com.github.mgurov.jdbcplayground;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.flywaydb.core.Flyway;

/**
 * Popup Tomcat, migrate DB and start LogService
 *
 * @author roland
 * @since 08.08.14
 */
public class LogService extends HttpServlet {

    // Fireup tomcat and register this servlet
    public static void main(String[] args) throws LifecycleException, SQLException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        File base = new File(System.getProperty("java.io.tmpdir"));
        Context rootCtx = tomcat.addContext("/", base.getAbsolutePath());
        Tomcat.addServlet(rootCtx, "log", new LogService());
        rootCtx.addServletMapping("/*", "log");
        tomcat.start();
        tomcat.getServer().await();
    }

    // Log into DB and print out all logs.
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = ConnectionManager.makeConnection()) {
            // Insert current request in DB ...
            insertLog(req, connection);

            // ... and then return all logs stored so far
            resp.setContentType("text/plain");
            PrintWriter out = resp.getWriter();
            printOutLogs(connection, out);

        } catch (SQLException e) {
            throw new ServletException("Cannot update DB: " + e,e);
        }
    }

    // Init DB and create table if required
    public LogService() throws SQLException {
        Flyway flyway = new Flyway();
        flyway.setDataSource(ConnectionManager.getConnectionUrl(), "postgres", null);
        flyway.migrate();
    }


    // ===================================================================================
    // Helper methods

    private void printOutLogs(Connection connection, PrintWriter out) throws SQLException {

        LogDao dao = new LogDaoJdbc(connection);
        dao.listLogs().stream()
                .map(l -> String.format("%s\t\t%s\t\t%s", l.timestamp, l.remoteAddress, l.requestURI))
                .forEach(out::println);

    }

    private void insertLog(HttpServletRequest req, Connection connection) throws SQLException {

        LogDao dao = new LogDaoJdbc(connection);
        dao.insertLog(
                new LogEntry(
                        new Date(),
                        req.getRemoteAddr(),
                        req.getRequestURI()
                )
        );
    }


}
