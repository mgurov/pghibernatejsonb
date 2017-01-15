package com.github.mgurov.jdbcplayground;

import java.util.List;

public interface LogDao {
    void insertLog(LogEntry logEntry);

    List<LogEntry> listLogs();

    void clearAll();
}
