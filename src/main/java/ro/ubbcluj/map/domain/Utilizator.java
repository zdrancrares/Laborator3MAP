package ro.ubbcluj.map.domain;


import ro.ubbcluj.map.domain.validators.ValidationException;

import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private List<Utilizator> friends;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Utilizator> getFriends() {
        return friends;
    }

    public void addFriend(Utilizator friend) throws ValidationException{
        if (friend == null){
            throw new ValidationException("Prietenul nu poate sa fie null.");
        }
        if (friends.contains(friend)){
            throw new ValidationException("Sunt deja prieteni.");
        }
        friends.add(friend);
    }

    public void removeFriend(Long id) throws ValidationException{
        for (var friend: friends){
            if (friend.getId() == id){
                friends.remove(friend);
                return;
            }
        }
        throw new ValidationException("Nu sunt prieteni.");
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friends +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}