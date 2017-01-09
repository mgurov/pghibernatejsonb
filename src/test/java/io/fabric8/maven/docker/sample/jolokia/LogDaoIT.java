package io.fabric8.maven.docker.sample.jolokia;

import io.fabric8.docker.sample.demo.*;
import org.junit.Ignore;
import org.junit.Test;
import org.postgresql.jdbc2.optional.SimpleDataSource;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore // should either run from inside the container or get the port somehow
public class LogDaoIT {

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

    private LogDao dao() {
        //return new LogDaoJdbc(ConnectionManager.makeConnection());
        //return new LogDaoSpringJdbc(ConnectionManager.makeDatasource());
        return new LogDaoHiberJpa();
    }

}
