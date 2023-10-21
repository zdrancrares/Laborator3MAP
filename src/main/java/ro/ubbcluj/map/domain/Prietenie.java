package ro.ubbcluj.map.domain;

import java.time.LocalDateTime;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;
    Utilizator user1;
    Utilizator user2;

    public Prietenie(Utilizator user1, Utilizator user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.date = LocalDateTime.now();
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
}
