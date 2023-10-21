package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.exceptions.RepositoryExceptions;
import ro.ubbcluj.map.exceptions.ServiceExceptions;
import ro.ubbcluj.map.repository.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserService implements Service<Long, Utilizator>{
    private Repository<Long, Utilizator> userRepo;
    private Repository<Tuple<Long,Long>, Prietenie> prietenieRepo;
    public UserService(Repository<Long, Utilizator> userRepo, Repository<Tuple<Long, Long>, Prietenie> prietenieRepo){
        this.userRepo = userRepo;
        this.prietenieRepo = prietenieRepo;
    }

    public boolean addFriend(Long userID, Long friendID) throws RepositoryExceptions{
        Utilizator user = userRepo.findOne(userID);
        Utilizator friend = userRepo.findOne(friendID);
        user.addFriend(friend);
        friend.addFriend(user);
        List<Utilizator> friendList = user.getFriends();
        return friendList.contains(friend);
    }

    @Override
    public boolean addEntity(Utilizator entity) throws RepositoryExceptions {
        Utilizator user = userRepo.save(entity);
        return user == null;
    }

    @Override
    public Utilizator deleteEntity(Long id) throws ServiceExceptions, RepositoryExceptions {
        Utilizator userToDelete =  userRepo.delete(id);
        if (userToDelete != null){
            for(Utilizator f: userToDelete.getFriends()){
                Tuple<Long, Long> newID = new Tuple<>(f.getId(), id);
                Tuple<Long, Long> newID2 = new Tuple<>(id, f.getId());
                prietenieRepo.delete(newID);
                prietenieRepo.delete(newID2);
                f.getFriends().remove(userToDelete);
            }
            return userToDelete;
        }
        throw new ServiceExceptions("Utilizatorul pe care doriti sa-l stergeti nu exista.");
    }

    public List<Utilizator> DFS(Utilizator user, Set<Utilizator> set){
        List<Utilizator> users = new ArrayList<>();
        users.add(user);
        set.add(user);
        for (Utilizator u: user.getFriends()){
            if (!set.contains(u)){
                List<Utilizator> list = DFS(u, set);
                users.addAll(list);
                DFS(u, set);
            }
        }
        return users;
    }

    public int noOfCommunities(){
        Iterable<Utilizator> users = userRepo.findAll();
        Set<Utilizator> set = new HashSet<>();
        int count = 0;
        for (Utilizator user: users){
            if (!set.contains(user)){
                count++;
                DFS(user, set);
            }
        }
        return count;
    }

    public List<Iterable<Utilizator>> mostSociableCommunity(){
        List<Iterable<Utilizator>> result = new ArrayList<>();
        Iterable<Utilizator> users = userRepo.findAll();
        Set<Utilizator> set = new HashSet<>();

        int max = -1;
        for (Utilizator u: users){
            if (!set.contains(u)){
                List<Utilizator> aux = DFS(u, set);
                if (aux.size() > max){
                    max = aux.size();
                    result = new ArrayList<>();
                    result.add(aux);
                }
                else if (aux.size() == max){
                    result.add(aux);
                }
            }
        }
        return result;
    }

    @Override
    public Iterable<Utilizator> getAll() {
        return userRepo.findAll();
    }

    public Utilizator getEntity(Long id) throws RepositoryExceptions{
        return userRepo.findOne(id);
    }

}
