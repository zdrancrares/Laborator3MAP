package ro.ubbcluj.map.service;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.exceptions.RepositoryExceptions;
import ro.ubbcluj.map.exceptions.ServiceExceptions;
import ro.ubbcluj.map.repository.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class UserService implements Service<Long, Utilizator>{
    private Repository<Long, Utilizator> userRepo;
    private Repository<Tuple<Long,Long>, Prietenie> prietenieRepo;

    private static Long usersID;
    public UserService(Repository<Long, Utilizator> userRepo, Repository<Tuple<Long, Long>, Prietenie> prietenieRepo){
        usersID = 0L;
        this.userRepo = userRepo;
        this.prietenieRepo = prietenieRepo;
    }

    /**
     * adds a friend to a certain user(it also adds it to the other user)
     *
     * @param userID:   the first user in the friendship
     * @param friendID: the second user in the friendship
     * @return true: if the two users are friends
     * otherwise it returns false
     * @throws RepositoryExceptions if either one of the users doesn't exist
     */

    public Optional<Boolean> addFriend(Long userID, Long friendID) throws RepositoryExceptions{
        Optional<Utilizator> user = userRepo.findOne(userID);
        Optional<Utilizator> friend = userRepo.findOne(friendID);

        Optional<Boolean> result = Optional.of(false);
        Predicate<Optional<Utilizator>> isPresentTest = Optional::isPresent;

        if (isPresentTest.test(user) && isPresentTest.test(friend)){
            user.get().addFriend(friend.get());
            friend.get().addFriend(user.get());
            List<Utilizator> friendList = user.get().getFriends();
            result = Optional.of(friendList.contains(friend.get()));
        }
        return result;
    }

    /**
     * generates an unique ID using an incrementing static variable
     * @return an ID(Long)
     */
    private Long generateID(){
        usersID += 1;
        return usersID;
    }

    /**
     * creates an entity with firstName and lastName
     * adds the entity if it's valid, and it isn't already saved
     * @param firstName
     *         the first name of the entity to be created and saved
     * @param lastName
     *          the last name of the entity to be saved
     * @return true - if the entity is saved
     *         otherwise returns false(id already exists)
     * @throws RepositoryExceptions from Repository
     *            if the entity is not valid
     *
     */

    public boolean addEntity(String firstName, String lastName) throws RepositoryExceptions {
        Utilizator entity = new Utilizator(firstName, lastName);
        entity.setId(generateID());
        return userRepo.save(entity).isEmpty();
    }

    @Override
    public Utilizator deleteEntity(Long id) throws ServiceExceptions, RepositoryExceptions {
        Optional<Utilizator> userToDelete = userRepo.delete(id);

        if (userToDelete.isPresent()) {
            userToDelete.get().getFriends().forEach(friend -> {
                try {
                    Tuple<Long, Long> newID = new Tuple<>(friend.getId(), id);
                    Tuple<Long, Long> newID2 = new Tuple<>(id, friend.getId());
                    prietenieRepo.delete(newID);
                    prietenieRepo.delete(newID2);
                    friend.removeFriend(id);
                } catch (RepositoryExceptions e) {
                    System.out.println(e.getMessage());
                }
            });
            return userToDelete.get();
        }
        throw new ServiceExceptions("Utilizatorul pe care doriti sa-l stergeti nu exista.");
    }

    public boolean updateEntity(Long id, String firstName, String lastName) throws RepositoryExceptions{
        Utilizator entity = new Utilizator(firstName, lastName);
        entity.setId(id);
        return userRepo.update(entity).isEmpty();
    }

    public Iterable<Utilizator> loadUserFriends(Long id) throws RepositoryExceptions{
        Optional<Utilizator> user = userRepo.loadFriends(id);
        return user.<Iterable<Utilizator>>map(Utilizator::getFriends).orElse(null);
    }

    /**
     * Depth First Search to find the users of a community
     * @param utilizator: the user we reached with searching
     * @param set: the set of users so we won't visit them twice
     * @return the users who form a community starting from 'user'
     */

    public List<Utilizator> DFS(Utilizator utilizator, Set<Utilizator> set){
        List<Utilizator> users = new ArrayList<>();
        Stack<Utilizator> stack = new Stack<>();

        stack.push(utilizator);
        set.add(utilizator);

        while (!stack.isEmpty()) {
            Utilizator current = stack.pop();
            users.add(current);

            current.getFriends().stream()
                    .filter(u -> !set.contains(u))
                    .forEach(u -> {
                        stack.push(u);
                        set.add(u);
                    });
        }
        return users;
    }

    /**
     * function which calculates the total number of communities in our social network(using DFS for finding communities)
     * @return the number of distinct communities in the network
     */
    public int noOfCommunities(){
        Set<Utilizator> set = new HashSet<>();
        AtomicInteger count = new AtomicInteger(0);

        userRepo.findAll().forEach(u -> {
            if (!set.contains(u)) {
                count.incrementAndGet();
                DFS(u, set);
            }
        });

        return count.get();
    }

    /**
     * function which finds the most sociable community/communities in our network
     * @return a list of iterable's with the most sociable communities
     */
    public List<Iterable<Utilizator>> mostSociableCommunity(){

        Iterable<Utilizator> users = userRepo.findAll();

        Set<Utilizator> set = new HashSet<>();
        List<Iterable<Utilizator>> result = new ArrayList<>();
        AtomicInteger maxLength = new AtomicInteger(-1);

        users.forEach(u -> {
            if (!set.contains(u)) {
                List<Utilizator> community = DFS(u, set);
                int friendsCounter = community.stream()
                        .mapToInt(c -> c.getFriends().size())
                        .sum();

                if (friendsCounter > maxLength.get()) {
                    maxLength.set(friendsCounter);
                    result.clear();
                    result.add(community);
                } else if (friendsCounter == maxLength.get()) {
                    result.add(community);
                }
            }
        });
        return result;
    }

    @Override
    public Iterable<Utilizator> getAll() {
        return userRepo.findAll();
    }

    /**
     * function which finds an entity by its id
     * @return the entity if there is an entity with this id
     *          otherwise it returns null
     */
    public Optional<Utilizator> getEntity(Long id) throws RepositoryExceptions{
        return userRepo.findOne(id);
    }

}
