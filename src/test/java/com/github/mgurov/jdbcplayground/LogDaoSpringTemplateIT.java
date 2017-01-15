package com.github.mgurov.jdbcplayground;

/**
 * Test talking directly to a postgress database
 */
public class LogDaoSpringTemplateIT extends LogDaoIT {

    @Override
    protected LogDao dao() {
        return new LogDaoSpringJdbc(ConnectionManager.makeDatasource());
    }
}
