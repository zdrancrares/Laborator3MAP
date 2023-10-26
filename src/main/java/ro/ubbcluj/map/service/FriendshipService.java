package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.exceptions.RepositoryExceptions;
import ro.ubbcluj.map.exceptions.ServiceExceptions;
import ro.ubbcluj.map.repository.Repository;

import java.util.Optional;


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
        Optional<Prietenie> friendship1 = friendshipRepo.findOne(entity.getId());
        Optional<Prietenie> friendship2 = friendshipRepo.findOne(newID);
        if (friendship1.isPresent() || friendship2.isPresent()){
            throw new ServiceExceptions("Prietenia exista deja");
        }
        Optional<Prietenie> friendship = friendshipRepo.save(entity);
        return friendship.isEmpty();
    }

    @Override
    public Prietenie deleteEntity(Tuple<Long, Long> id) throws ServiceExceptions, RepositoryExceptions {
        Optional<Prietenie> friendship1 = friendshipRepo.delete(id);
        Long firstID = id.getLeft();
        Long secondID = id.getRight();
        Tuple<Long, Long> newID = new Tuple<>(secondID, firstID);
        Optional<Prietenie> friendship2 = friendshipRepo.delete(newID);
        if (friendship1.isPresent()){
            friendship1.get().getUser1().removeFriend(friendship1.get().getUser2().getId());
            friendship1.get().getUser2().removeFriend(friendship1.get().getUser1().getId());
            return friendship1.get();
        }
        if (friendship2.isPresent()){
            friendship2.get().getUser1().removeFriend(friendship2.get().getUser2().getId());
            friendship2.get().getUser2().removeFriend(friendship2.get().getUser1().getId());
            return friendship2.get();
        }
        throw new ServiceExceptions("Nu exista aceasta prietenie!");
    }

    @Override
    public Iterable<Prietenie> getAll() {
        return friendshipRepo.findAll();
    }
}
