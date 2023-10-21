package ro.ubbcluj.map;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.domain.validators.PrietenieValidator;
import ro.ubbcluj.map.domain.validators.UtilizatorValidator;
import ro.ubbcluj.map.domain.validators.Validator;
import ro.ubbcluj.map.exceptions.RepositoryExceptions;
import ro.ubbcluj.map.exceptions.ServiceExceptions;
import ro.ubbcluj.map.presentation.ConsoleUI;
import ro.ubbcluj.map.repository.InMemoryRepository;
import ro.ubbcluj.map.service.FriendshipService;
import ro.ubbcluj.map.service.Service;
import ro.ubbcluj.map.service.UserService;

public class Main {
    public static void main(String[] args) throws RepositoryExceptions, ServiceExceptions {
        Validator<Utilizator> userValidator = new UtilizatorValidator();
        Validator<Prietenie> friendshipValidator = new PrietenieValidator();

        InMemoryRepository<Long, Utilizator> repoUser =  new InMemoryRepository<>(userValidator);
        InMemoryRepository<Tuple<Long,Long>, Prietenie> repoFriendship= new InMemoryRepository<>(friendshipValidator);

        UserService userService = new UserService(repoUser, repoFriendship);
        FriendshipService friendshipService = new FriendshipService(repoFriendship);

        ConsoleUI console = new ConsoleUI(userService, friendshipService);
        console.startConsole();
    }
}
