package com.github.mgurov.jdbcplayground;


import com.google.gson.Gson;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class LogDaoSpringJdbc implements LogDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LogDaoSpringJdbc(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void insertLog(LogEntry logEntry) {

        Map<String, Object> data = new HashMap<>();
        data.put("remoteAddr", logEntry.remoteAddress);
        data.put("requestURI", logEntry.requestURI);

        PGobject jsonB = new PGobject();
        try {
            jsonB.setValue(gson.toJson(data));
        } catch (SQLException e) {
            throw Throwables.propagate(e);
        }
        jsonB.setType("jsonb");

        Map<String, Object> params = new HashMap<>();
        params.put("date", new Timestamp(logEntry.timestamp.getTime()));
        params.put("data", jsonB);

        jdbcTemplate.update(
                "INSERT INTO LOGGING (date, data) VALUES (:date, :data)",
                params
        );
    }

    @Override
    public List<LogEntry> listLogs() {

        return jdbcTemplate.query("SELECT * FROM LOGGING ORDER BY DATE ASC", (rs, i) -> {
            Timestamp date = rs.getTimestamp("DATE");
            Map<String, Object> data = gson.fromJson(rs.getString("data"), Map.class);
            return new LogEntry(
                    new java.util.Date(date.getTime()),
                    data.get("remoteAddr").toString(),
                    data.get("requestURI").toString());
        });
    }


    private final static Gson gson = new Gson();

    @Override
    public void clearAll() {
        jdbcTemplate.update("DELETE FROM LOGGING", Collections.emptyMap());
    }
}
