package com.github.mgurov.jdbcplayground;

/**
 * Test talking directly to a postgress database
 */
public class LogDaoPlainJdbcIT extends LogDaoIT {

    @Override
    protected LogDao dao() {
        return new LogDaoJdbc(ConnectionManager.makeDatasource());
    }
}
