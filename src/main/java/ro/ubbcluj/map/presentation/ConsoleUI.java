package ro.ubbcluj.map.presentation;

import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.service.UserService;

import java.util.Scanner;

public class ConsoleUI<ID> {
    private Scanner scanner;
    public UserService service;
    public ConsoleUI(UserService service){
        this.service = service;
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
        System.out.print("User's ID: ");
        Long userID = scanner.nextLong();
        System.out.print("Friend's ID: ");
        Long friendID = scanner.nextLong();
        try {
            service.addFriend(userID, friendID);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void getAllFriends(){
        System.out.print("User's ID: ");
        Long userID = scanner.nextLong();
        try{
            Iterable<Utilizator> friends = service.getAllFriends(userID);
            for (Utilizator f: friends){
                System.out.println(f);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void startConsole(){
        boolean run = true;
        while (run){
            boolean checkCommand = true;
            int command = 0;
            while(checkCommand) {
                try {
                    System.out.print("Introduceti comanda: ");
                    command = scanner.nextInt();
                    checkCommand = false;
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


            }
        }
    }
}
