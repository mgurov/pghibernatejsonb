package com.github.mgurov.jdbcplayground;

/**
 * inspired by guava
 */
public class Throwables {
    public static RuntimeException propagate(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        } else {
            throw new RuntimeException(e);
        }
    }
}
