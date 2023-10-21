package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Entity;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class UserService implements Service<Long, Utilizator>{
    public Repository<Long,Utilizator> userRepo;
    public UserService(Repository<Long, Utilizator> repo){
        userRepo = repo;
    }
    public boolean addFriend(Long userID, Long friendID){
        Utilizator user = userRepo.findOne(userID);
        Utilizator friend = userRepo.findOne(friendID);
        user.addFriend(friend);
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
    public Utilizator deleteEntity(Long id) {
        return userRepo.delete(id);
    }

    @Override
    public Iterable<Utilizator> getAll() {
        return userRepo.findAll();
    }

    public Iterable<Utilizator> getAllFriends(Long userID){
        Utilizator user = userRepo.findOne(userID);
        return user.getFriends();
    }
}
