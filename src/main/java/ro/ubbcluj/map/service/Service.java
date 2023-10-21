package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Entity;
import ro.ubbcluj.map.domain.Utilizator;

public interface Service<ID, E extends Entity<ID>> {
    boolean addEntity(E entity);

    E deleteEntity(ID id);

    Iterable<E> getAll();
}
