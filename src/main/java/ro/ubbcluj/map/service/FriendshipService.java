package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.validators.Validator;
import ro.ubbcluj.map.exceptions.ServiceExceptions;
import ro.ubbcluj.map.repository.Repository;


public class FriendshipService implements Service<Tuple<Long,Long>, Prietenie>{
    public Repository<Tuple<Long, Long>, Prietenie> friendshipRepo;
    public FriendshipService(Repository<Tuple<Long,Long>, Prietenie> friendshipRepo){
        this.friendshipRepo = friendshipRepo;
    }

    @Override
    public boolean addEntity(Prietenie entity) throws ServiceExceptions{
        Long id1 = entity.getId().getLeft();
        Long id2 = entity.getId().getRight();
        Tuple<Long, Long> newID = new Tuple<>(id2,id1);
        Prietenie gasestePrietenie1 = friendshipRepo.findOne(entity.getId());
        Prietenie gasestePrietenie2 = friendshipRepo.findOne(newID);
        if (gasestePrietenie1 != null || gasestePrietenie2 != null){
            throw new ServiceExceptions("Prietenia exista deja");
        }
        Prietenie friendship = friendshipRepo.save(entity);
        return friendship == null;
    }

    @Override
    public Prietenie deleteEntity(Tuple<Long, Long> id) throws ServiceExceptions {
        Prietenie friendship = friendshipRepo.delete(id);
        if (friendship != null){
            friendship.getUser1().getFriends().remove(friendship.getUser2());
            friendship.getUser2().getFriends().remove(friendship.getUser1());
            return friendship;
        }
        throw new ServiceExceptions("Nu exista aceasta prietenie!");
    }

    @Override
    public Iterable<Prietenie> getAll() {
        return friendshipRepo.findAll();
    }
}
