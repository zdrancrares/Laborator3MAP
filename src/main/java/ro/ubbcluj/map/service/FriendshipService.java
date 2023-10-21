package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.repository.Repository;


public class FriendshipService implements Service<Tuple<Long,Long>, Prietenie>{
    public Repository<Tuple<Long, Long>, Prietenie> friendshipRepo;
    public FriendshipService(Repository<Tuple<Long,Long>, Prietenie> friendshipRepo){
        this.friendshipRepo = friendshipRepo;
    }

    @Override
    public boolean addEntity(Prietenie entity) {
        Prietenie friendship = friendshipRepo.save(entity);
        return friendship == null;
    }

    @Override
    public Prietenie deleteEntity(Tuple<Long, Long> id) {
        return friendshipRepo.delete(id);
    }

    @Override
    public Iterable<Prietenie> getAll() {
        return friendshipRepo.findAll();
    }
}
