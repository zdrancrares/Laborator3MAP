package ro.ubbcluj.map.repository;

import ro.ubbcluj.map.config.DatabaseConnectionConfig;
import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.domain.validators.Validator;
import ro.ubbcluj.map.exceptions.RepositoryExceptions;
import ro.ubbcluj.map.service.FriendshipService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class FriendshipDBRepository implements Repository<Tuple<Long,Long>, Prietenie> {
    private Validator<Prietenie> validator;
    public FriendshipDBRepository(Validator<Prietenie> validator){
        this.validator = validator;
    }
    @Override
    public Optional<Prietenie> findOne(Tuple<Long, Long> id) throws RepositoryExceptions {
        Predicate<Tuple<Long, Long>> isNull = Objects::isNull;
        if (isNull.test(id)) {
            throw new RepositoryExceptions("ID-ul nu poate fi null.");
        }

        String findFriendship = "select * from friendships F " +
                "inner join users U1 on F.userid1=U1.id " +
                "inner join users U2 on F.userid2=U2.id "+
                "where F.userid1=? AND F.userid2=?";

        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement getFriendshipStatement = connection.prepareStatement(findFriendship, Statement.RETURN_GENERATED_KEYS);
        ){
            getFriendshipStatement.setLong(1,id.getLeft());
            getFriendshipStatement.setLong(2,id.getRight());
            ResultSet searchedFriendship = getFriendshipStatement.executeQuery();

            if (searchedFriendship.next()){
                Prietenie p= new Prietenie(null, null);
                Tuple<Long,Long> idPrietenie = new Tuple<>(id.getLeft(), id.getRight());
                p.setId(idPrietenie);
                p.setDate(searchedFriendship.getTimestamp(3).toLocalDateTime());

                Utilizator u1 = new Utilizator(null, null);
                u1.setFirstName(searchedFriendship.getString(5));
                u1.setLastName(searchedFriendship.getString(6));
                u1.setId(searchedFriendship.getLong(4));

                Utilizator u2 = new Utilizator(null, null);
                u2.setFirstName(searchedFriendship.getString(8));
                u2.setLastName(searchedFriendship.getString(9));
                u2.setId(searchedFriendship.getLong(7));

                p.setUser1(u1);
                p.setUser2(u2);
                return Optional.of(p);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Prietenie> findAll() {
        HashSet<Prietenie> friendships = new HashSet<>();
        String selectFriendshipsStatement = "select * from friendships F " +
                "inner join users U1 on U1.id=F.userid1 " +
                "inner join users U2 on U2.id=F.userid2";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement getFriendshipsStatement = connection.prepareStatement(selectFriendshipsStatement);
            ResultSet friendshipsResultSet = getFriendshipsStatement.executeQuery();
        ){
            while(friendshipsResultSet.next()){
                Prietenie p = new Prietenie(null,null);

                Utilizator u1 = new Utilizator(null, null);
                u1.setId(friendshipsResultSet.getLong(4));
                u1.setFirstName(friendshipsResultSet.getString(5));
                u1.setLastName(friendshipsResultSet.getString(6));

                Utilizator u2 = new Utilizator(null, null);
                u2.setId(friendshipsResultSet.getLong(7));
                u2.setFirstName(friendshipsResultSet.getString(8));
                u2.setLastName(friendshipsResultSet.getString(9));

                p.setDate(friendshipsResultSet.getTimestamp(3).toLocalDateTime());
                p.setUser1(u1);
                p.setUser2(u2);
                Tuple<Long, Long> id = new Tuple<>(u1.getId(), u2.getId());
                p.setId(id);

                friendships.add(p);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return friendships;
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) throws RepositoryExceptions {
        Predicate<Prietenie> isNull = Objects::isNull;
        if (isNull.test(entity)) {
            throw new RepositoryExceptions("Entitatea nu poate fi null.");
        }
        validator.validate(entity);
        String insertSqlStatement = "insert into friendships(userid1, userid2,friendsFrom) values (?,?,?)";
        Optional<Prietenie> result = Optional.of(entity);
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement insertStatement = connection.prepareStatement(insertSqlStatement, Statement.RETURN_GENERATED_KEYS);
        ){
            insertStatement.setLong(1,entity.getUser1().getId());
            insertStatement.setLong(2,entity.getUser2().getId());
            insertStatement.setTimestamp(3,Timestamp.valueOf(entity.getDate()));
            if (insertStatement.executeUpdate() > 0){
                ResultSet resultSet = insertStatement.getGeneratedKeys();
                resultSet.next();
                Long id1 = resultSet.getLong("userid1");
                Long id2 = resultSet.getLong("userid2");
                Tuple<Long, Long> id = new Tuple<>(id1, id2);
                entity.setId(id);
                result = Optional.empty();
            }
        }catch (SQLException e){
            e.getMessage();
        }
        return result;
    }

    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> longLongTuple) throws RepositoryExceptions {
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) throws RepositoryExceptions {
        Predicate<Prietenie> isNull = Objects::isNull;
        if (isNull.test(entity)) {
            throw new RepositoryExceptions("Entitatea nu poate fi null.");
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> loadFriends(Tuple<Long, Long> idUser) {
        return Optional.empty();
    }

    @Override
    public Iterable<Prietenie> loadFriendsMonth(Tuple<Long, Long> idUser, int month) throws RepositoryExceptions {
        return null;
    }
}
