package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Entity;

public interface Service<ID, E extends Entity<ID>> {
    boolean addEntity(E entity);

    E deleteEntity(ID id);

    Iterable<E> getAll();
}
