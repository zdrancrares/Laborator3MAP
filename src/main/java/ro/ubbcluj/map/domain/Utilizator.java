package ro.ubbcluj.map.domain;


import com.jogamp.common.util.Bitfield;
import ro.ubbcluj.map.domain.validators.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private final ArrayList<Utilizator> friends;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        friends = new ArrayList<>();
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
        for (Utilizator friend: friends){
            if (friend.getId() == id){
                friends.remove(friend);
                return;
            }
        }
        throw new ValidationException("Nu sunt prieteni.");
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends: ");
        for(Utilizator utilizator: friends) {
            result.append(utilizator.firstName)
                    .append(" ")
                    .append(utilizator.getLastName())
                    .append(",");
        }
        result = new StringBuilder(result.substring(0, result.length() - 1));
        result.append("}");
        return result.toString();
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