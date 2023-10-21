package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Entity;

public interface Service<ID, E extends Entity<ID>> {
    boolean addEntity(E entity) throws Exception;

    E deleteEntity(ID id) throws Exception;

    Iterable<E> getAll();
}
