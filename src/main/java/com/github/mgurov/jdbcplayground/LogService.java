package com.github.mgurov.jdbcplayground;

import java.io.*;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

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

    private final LogDao dao = new LogDaoJdbc(ConnectionManager.makeDatasource());


    // Log into DB and print out all logs.
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        insertLog(req);

        // ... and then return all logs stored so far
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        printOutLogs(out);
    }


    private void printOutLogs(PrintWriter out) {
        dao.listLogs().stream()
                .map(l -> String.format("%s\t\t%s\t\t%s", l.timestamp, l.remoteAddress, l.requestURI))
                .forEach(out::println);

    }

    private void insertLog(HttpServletRequest req) {
        dao.insertLog(
                new LogEntry(
                        new Date(),
                        req.getRemoteAddr(),
                        req.getRequestURI()
                )
        );
    }


}
