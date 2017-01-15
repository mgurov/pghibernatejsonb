package com.github.mgurov.jdbcplayground;

import javax.persistence.*;
import java.util.List;
import java.util.function.Function;

public class LogDaoHiberJpa implements LogDao {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("TEST");

    public LogDaoHiberJpa() {
    }

    @Override
    public void insertLog(LogEntry logEntry) {
        withTransaction((em) -> {
            em.persist(logEntry);
            em.flush();
            return Void.TYPE;
        });
    }

    @Override
    public List<LogEntry> listLogs() {
        return withEntityManager((entityManager) -> {
            TypedQuery<LogEntry> query = entityManager.createQuery("Select l from LogEntry l", LogEntry.class);
            return query.getResultList();
        });
    }

    @Override
    public void clearAll() {
        withTransaction((em) -> em.createNativeQuery("DELETE FROM LOGGING").executeUpdate());
    }

    private <T> T withEntityManager(Function<EntityManager, T> cb) {
        EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
        try {
            return cb.apply(entityManager);
        } finally {
            entityManager.close();
        }
    }

    private <T> T withTransaction(Function<EntityManager, T> cb) {
        return withEntityManager((entityManager) -> {
            EntityTransaction transaction = null;
            try {
                transaction = entityManager.getTransaction();
                transaction.begin();

                T result = cb.apply(entityManager);

                transaction.commit();
                transaction = null;
                return result;
            } catch (RuntimeException re) {
                if (null != transaction) {
                    transaction.rollback();
                }
                throw re;
            }
        });
    }
}
