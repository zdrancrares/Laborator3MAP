package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.exceptions.ServiceExceptions;
import ro.ubbcluj.map.repository.Repository;

import java.util.List;

public class UserService implements Service<Long, Utilizator>{
    public Repository<Long, Utilizator> userRepo;
    public UserService(Repository<Long, Utilizator> userRepo){
        this.userRepo = userRepo;

    }

    public boolean addFriend(Long userID, Long friendID){
        Utilizator user = userRepo.findOne(userID);
        Utilizator friend = userRepo.findOne(friendID);
        user.addFriend(friend);
        friend.addFriend(user);
        List<Utilizator> friendList = user.getFriends();
        return friendList.contains(friend);
    }

    public boolean removeFriend(Long userID, Long friendID){
        Utilizator user = userRepo.findOne(userID);
        Utilizator friend = userRepo.findOne(friendID);
        user.removeFriend(friendID);
        List<Utilizator> friendList = user.getFriends();
        return !friendList.contains(friend);
    }

    @Override
    public boolean addEntity(Utilizator entity) {
        Utilizator user = userRepo.save(entity);
        return user == null;
    }


    @Override
    public Utilizator deleteEntity(Long id) throws ServiceExceptions{
        Utilizator userToDelete =  userRepo.delete(id);
        if (userToDelete != null){
            for(Utilizator f: userToDelete.getFriends()){
                f.getFriends().remove(userToDelete);
            }
            return userToDelete;
        }
        throw new ServiceExceptions("Utilizatorul pe care doriti sa-l stergeti nu exista.");
    }

    @Override
    public Iterable<Utilizator> getAll() {
        return userRepo.findAll();
    }

    public Utilizator getEntity(Long id){
        return userRepo.findOne(id);
    }

    public Iterable<Utilizator> getAllFriends(Long userID){
        Utilizator user = userRepo.findOne(userID);
        return user.getFriends();
    }
}
