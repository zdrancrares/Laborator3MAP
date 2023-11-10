package ro.ubbcluj.map;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.domain.validators.PrietenieValidator;
import ro.ubbcluj.map.domain.validators.UtilizatorValidator;
import ro.ubbcluj.map.domain.validators.Validator;
import ro.ubbcluj.map.exceptions.RepositoryExceptions;
import ro.ubbcluj.map.presentation.ConsoleUI;
import ro.ubbcluj.map.repository.FriendshipDBRepository;
import ro.ubbcluj.map.repository.InMemoryRepository;
import ro.ubbcluj.map.repository.Repository;
import ro.ubbcluj.map.repository.UserDBRepository;
import ro.ubbcluj.map.service.FriendshipService;
import ro.ubbcluj.map.service.UserService;

import java.util.Optional;

public class Main {
    public static void main(String[] args) throws RepositoryExceptions {
        Validator<Utilizator> userValidator = new UtilizatorValidator();
        Validator<Prietenie> friendshipValidator = new PrietenieValidator();

        //InMemoryRepository<Long, Utilizator> repoUser =  new InMemoryRepository<>(userValidator);
        //InMemoryRepository<Tuple<Long,Long>, Prietenie> repoFriendship= new InMemoryRepository<>(friendshipValidator);

        Repository<Long, Utilizator> repoUserDB = new UserDBRepository(userValidator);
        Repository<Tuple<Long,Long>,Prietenie> repoFriendshipDB = new FriendshipDBRepository(friendshipValidator);

        UserService userService = new UserService(repoUserDB, repoFriendshipDB);
        FriendshipService friendshipService = new FriendshipService(repoFriendshipDB);

        ConsoleUI console = new ConsoleUI(userService, friendshipService);
        console.startConsole();

        //todo: loadFriends in userRepoDB - in principiu facuta
        //todo: cand adaug o prietenie intotdeauna id-urile vor fi sortate crescator
        //todo: friendshipRepoDB + de gatat la userRepoDB

        //Tuple<Long, Long> id = new Tuple<>(1L,2L);
        //System.out.println(repoFriendshipDB.findOne(id));
        //System.out.println(repoFriendshipDB.findAll());
        //Optional<Utilizator> u1 = repoUserDB.findOne(5L);
        //Optional<Utilizator> u2 = repoUserDB.findOne(6L);
        //Prietenie tobeSaved = new Prietenie(u1.get(), u2.get());
        //System.out.println(repoFriendshipDB.save(tobeSaved));
    }
}
