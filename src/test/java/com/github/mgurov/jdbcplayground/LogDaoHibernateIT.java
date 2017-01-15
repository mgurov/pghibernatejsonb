package com.github.mgurov.jdbcplayground;

/**
 * Test talking directly to a postgress database
 */
public class LogDaoHibernateIT extends LogDaoIT {

    @Override
    protected LogDao dao() {
        return new LogDaoHiberJpa();
    }
}
