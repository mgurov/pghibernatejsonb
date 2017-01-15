package com.github.mgurov.jdbcplayground;

import com.google.gson.Gson;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogDaoJdbc implements LogDao {
    private final DataSource dataSource;

    public LogDaoJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insertLog(LogEntry logEntry) {
        try {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt =
                         connection.prepareStatement("INSERT INTO LOGGING (date, data) VALUES (?, ?)")) {
                Map<String, Object> data = new HashMap<>();
                data.put("remoteAddr", logEntry.remoteAddress);
                data.put("requestURI", logEntry.requestURI);
                stmt.setTimestamp(1, new Timestamp(logEntry.timestamp.getTime()));
                PGobject jsonB = new PGobject();//
                jsonB.setValue(gson.toJson(data));
                jsonB.setType("jsonb");
                stmt.setObject(2,  jsonB);
                stmt.executeUpdate();
            }
        } catch (SQLException se) {
            throw Throwables.propagate(se);
        }
    }

    @Override
    public List<LogEntry> listLogs() {

        try {

            List<LogEntry> r = new ArrayList<>();
            try (
                    Connection connection = dataSource.getConnection();
                    Statement select = connection.createStatement();
                    ResultSet result = select.executeQuery("SELECT * FROM LOGGING ORDER BY DATE ASC");
            ) {
                while (result.next()) {
                    Timestamp date = result.getTimestamp("DATE");
                    Map<String, Object> data = gson.fromJson(result.getString("data"), Map.class);
                    r.add(
                            new LogEntry(
                                    new java.util.Date(date.getTime()),
                                    data.get("remoteAddr") + "",
                                    data.get("requestURI") + ""))
                    ;
                }
            }
            return r;
        } catch (SQLException se) {
            throw Throwables.propagate(se);
        }

    }


    private final static Gson gson = new Gson();

    @Override
    public void clearAll() {
        try {
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement stmt =
                         connection.prepareStatement("DELETE FROM LOGGING")) {
                stmt.executeUpdate();
            }
        } catch (SQLException se) {
            throw Throwables.propagate(se);
        }
    }
}
