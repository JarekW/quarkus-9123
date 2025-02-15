package org.acme;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Dependent
public class PostgresRepository {

    @Inject
    EntityManager em;

    public TestEntity findOne(String id) {
        return em.find(TestEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
    }

    public void save(TestEntity file) {
        em.persist(file);
        em.flush();
    }
}
