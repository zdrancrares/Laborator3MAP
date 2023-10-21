package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.exceptions.RepositoryExceptions;
import ro.ubbcluj.map.exceptions.ServiceExceptions;
import ro.ubbcluj.map.repository.Repository;


public class FriendshipService implements Service<Tuple<Long,Long>, Prietenie>{
    private Repository<Tuple<Long, Long>, Prietenie> friendshipRepo;
    public FriendshipService(Repository<Tuple<Long,Long>, Prietenie> friendshipRepo){
        this.friendshipRepo = friendshipRepo;
    }

    @Override
    public boolean addEntity(Prietenie entity) throws ServiceExceptions, RepositoryExceptions{
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
    public Prietenie deleteEntity(Tuple<Long, Long> id) throws ServiceExceptions, RepositoryExceptions {
        Prietenie friendship1 = friendshipRepo.delete(id);
        Long firstID = id.getLeft();
        Long secondID = id.getRight();
        Tuple<Long, Long> newID = new Tuple<>(secondID, firstID);
        Prietenie friendship2 = friendshipRepo.delete(newID);
        if (friendship1 != null){
            friendship1.getUser1().getFriends().remove(friendship1.getUser2());
            friendship1.getUser2().getFriends().remove(friendship1.getUser1());
            return friendship1;
        }
        if (friendship2 != null){
            friendship2.getUser1().getFriends().remove(friendship2.getUser2());
            friendship2.getUser2().getFriends().remove(friendship2.getUser1());
            return friendship2;
        }
        throw new ServiceExceptions("Nu exista aceasta prietenie!");
    }

    @Override
    public Iterable<Prietenie> getAll() {
        return friendshipRepo.findAll();
    }
}
