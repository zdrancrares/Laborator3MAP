package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.exceptions.RepositoryExceptions;
import ro.ubbcluj.map.exceptions.ServiceExceptions;
import ro.ubbcluj.map.repository.Repository;


public class FriendshipService implements Service<Tuple<Long,Long>, Prietenie>{
    private Repository<Tuple<Long, Long>, Prietenie> friendshipRepo;
    public FriendshipService(Repository<Tuple<Long,Long>, Prietenie> friendshipRepo){
        this.friendshipRepo = friendshipRepo;
    }


    /**
     * adds the friendship between the two given users if it's valid, and it isn't already saved
     * creates the id based on the users' id's
     * @param user1
     *         the first user of the friendship
     * @param user2
     *          the second user of the friendship
     * @return true - if the entity is saved
     *         otherwise returns false(id already exists)
     * @throws ServiceExceptions
     *            if the friendship is not valid
     *
     */

    public boolean addEntity(Utilizator user1, Utilizator user2) throws ServiceExceptions, RepositoryExceptions{
        Prietenie entity = new Prietenie(user1, user2);
        Tuple<Long, Long> prietenieID = new Tuple<>(entity.getUser1().getId(), entity.getUser2().getId());
        entity.setId(prietenieID);
        Long id1 = entity.getId().getLeft();
        Long id2 = entity.getId().getRight();
        Tuple<Long, Long> newID = new Tuple<>(id2,id1);
        Prietenie friendship1 = friendshipRepo.findOne(entity.getId());
        Prietenie friendship2 = friendshipRepo.findOne(newID);
        if (friendship1 != null || friendship2 != null){
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
            friendship1.getUser1().removeFriend(friendship1.getUser2().getId());
            friendship1.getUser2().removeFriend(friendship1.getUser1().getId());
            return friendship1;
        }
        if (friendship2 != null){
            friendship2.getUser1().removeFriend(friendship2.getUser2().getId());
            friendship2.getUser2().removeFriend(friendship2.getUser1().getId());
            return friendship2;
        }
        throw new ServiceExceptions("Nu exista aceasta prietenie!");
    }

    @Override
    public Iterable<Prietenie> getAll() {
        return friendshipRepo.findAll();
    }
}
