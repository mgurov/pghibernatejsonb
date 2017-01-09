package io.fabric8.docker.sample.demo;

import java.util.List;

public interface LogDao {
    void insertLog(LogEntry logEntry);

    List<LogEntry> listLogs();

    void clearAll();
}
