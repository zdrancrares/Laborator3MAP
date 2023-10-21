package ro.ubbcluj.map.presentation;

import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.service.FriendshipService;
import ro.ubbcluj.map.service.UserService;

import java.util.Scanner;

public class ConsoleUI {
    private Scanner scanner;
    private static Long usersID;
    public UserService userService;
    public FriendshipService friendshipService;
    public ConsoleUI(UserService userService, FriendshipService friendshipService){
        usersID = 0L;
        this.userService = userService;
        this.friendshipService = friendshipService;
        scanner = new Scanner(System.in);
    }
    private static void printMenu(){
        System.out.println("0 - Exit");
        System.out.println("1 - Show the menu again");
        System.out.println("2 - Add a user");
        System.out.println("3 - Remove a user");
        System.out.println("4 - Show all users");
        System.out.println("5 - Add a friend to a user");
        System.out.println("6 - Remove a friend from a user");
        System.out.println("7 - Show all friends of a user");
    }

    private void addFriend(){
        System.out.print("User_1's ID: ");
        Long user1ID = scanner.nextLong();
        System.out.print("User_2's ID: ");
        Long user2ID = scanner.nextLong();
        Utilizator user1 = userService.getEntity(user1ID);
        Utilizator user2 = userService.getEntity(user2ID);
        Prietenie prietenie = new Prietenie(user1, user2);
        try {
            if (friendshipService.addEntity(prietenie)) {
                System.out.println("Prietenia a fost formata cu succes.");
                user1.addFriend(user2);
                user2.addFriend(user1);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void removeFriend(){
    }

    private void showAllFriends(){

    }

    private Long generateID(){
        usersID += 1;
        return usersID;
    }

    private void addUser(){
        System.out.print("Enter the user's first name: ");
        String firstName = scanner.next();
        System.out.print("Enter the user's last name: ");
        String lastName = scanner.next();
        Utilizator user = new Utilizator(firstName, lastName);
        user.setId(generateID());
        try {
            userService.addEntity(user);
            System.out.println("Utilizatorul a fost adaugat cu succes.");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void deleteUser(){
        System.out.println("Enter the user's ID: ");
        Long userID = scanner.nextLong();
        try{
            userService.deleteEntity(userID);
            System.out.println("Utilizatorul a fost sters cu succes");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showAllUsers(){
        Iterable<Utilizator> users = userService.getAll();
        boolean found = false;
        for (Utilizator u: users){
            System.out.println(u);
            found = true;
        }
        if (!found){
            System.out.println("Nu exista niciun utilizator.");
        }
    }

    public void startConsole(){
        boolean run = true;
        ConsoleUI.printMenu();
        while (run){
            int command = 0;
            while(true) {
                try {
                   System.out.print("Introduceti comanda: ");
                   command = scanner.nextInt();
                   break;
                } catch (Exception e) {
                    System.out.println("Nu ati introdus o optiune valida.");
                }
            }

            switch (command){
                case 0:
                    System.out.println("Goodbye!");
                    return;
                case 1:
                    ConsoleUI.printMenu();
                    break;
                case 2:
                    addUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    showAllUsers();
                    break;
                case 5:
                    addFriend();
                    break;
                case 6:
                    removeFriend();
                    break;
                case 7:
                    showAllFriends();
                    break;
                default:
                    System.out.println("Nu ati introdus o optiune valida");
            }
        }
    }
}
