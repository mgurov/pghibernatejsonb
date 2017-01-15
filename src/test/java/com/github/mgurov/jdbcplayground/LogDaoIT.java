package com.github.mgurov.jdbcplayground;

import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test talking directly to a postgress database
 */
public abstract class LogDaoIT {

    @Test
    public void insertion() {
        LogDao dao = dao();
        dao.insertLog(new LogEntry(new Date(), "blah", "fooe"));
    }

    @Test
    public void readup() {
        LogDao dao = dao();
        System.out.println(dao.listLogs());
    }

    @Test
    public void saveAndRead() {
        LogDao dao = dao();
        dao.clearAll();
        LogEntry saved = new LogEntry(new Date(), "blah", "fooe");
        dao.insertLog(saved);
        List<LogEntry> read = dao.listLogs();
        assertEquals(Collections.singletonList(saved), read);
    }

    protected abstract LogDao dao();

}
