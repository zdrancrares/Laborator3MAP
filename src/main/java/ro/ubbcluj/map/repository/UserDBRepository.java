package ro.ubbcluj.map.repository;

import ro.ubbcluj.map.config.DatabaseConnectionConfig;
import ro.ubbcluj.map.domain.Prietenie;
import ro.ubbcluj.map.domain.Tuple;
import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.domain.validators.Validator;
import ro.ubbcluj.map.exceptions.RepositoryExceptions;

import java.sql.*;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserDBRepository implements Repository<Long, Utilizator> {
    private final Validator<Utilizator> validator;
    public UserDBRepository(Validator<Utilizator> validator){
        this.validator = validator;
    }
    @Override
    public Optional<Utilizator> findOne(Long aLong) throws RepositoryExceptions {
        Predicate<Long> isNull = Objects::isNull;
        if (isNull.test(aLong)) {
            throw new RepositoryExceptions("ID-ul nu poate fi null.");
        }
        String findUser = "select * from users where id=?";
        try (Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
             PreparedStatement getUserStatement = connection.prepareStatement(findUser);
        ){
            getUserStatement.setLong(1, aLong);
            ResultSet searchedUser = getUserStatement.executeQuery();
            if (searchedUser.next()){
                Utilizator user = new Utilizator(null, null);
                user.setId(searchedUser.getLong("id"));
                user.setFirstName(searchedUser.getString("first_name"));
                user.setLastName(searchedUser.getString("last_name"));
                return Optional.of(user);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        HashSet<Utilizator> users = new HashSet<>();
        String selectUsersStatement = "select * from users";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement getUsersStatement = connection.prepareStatement(selectUsersStatement);
            ResultSet usersResultSet = getUsersStatement.executeQuery();
        ){
            while(usersResultSet.next()){
                String firstName = usersResultSet.getString("first_name");
                String lastName = usersResultSet.getString("last_name");
                Long id = usersResultSet.getLong("id");
                Utilizator currentUser = new Utilizator(firstName, lastName);
                currentUser.setId(id);
                users.add(currentUser);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) throws RepositoryExceptions {
        Predicate<Utilizator> isNull = Objects::isNull;
        if (isNull.test(entity)){
            throw new RepositoryExceptions("Utilizatorul nu poate sa fie null");
        }
        validator.validate(entity);
        String insertSqlStatement = "insert into users(first_name,last_name) values(?,?)";
        Optional<Utilizator> result = Optional.empty();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement insertStatement = connection.prepareStatement(insertSqlStatement, Statement.RETURN_GENERATED_KEYS);
        ){
            insertStatement.setString(1, entity.getFirstName());
            insertStatement.setString(2, entity.getLastName());
            if (insertStatement.executeUpdate() > 0){
                ResultSet resultSet = insertStatement.getGeneratedKeys();
                resultSet.next();
                Long id = resultSet.getLong("id");
                entity.setId(id);
                result = Optional.empty();
            }
            else{
                //System.out.println("Nu s-a creat entitatea.");
                result = Optional.of(entity);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public Optional<Utilizator> delete(Long aLong) throws RepositoryExceptions {
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) throws RepositoryExceptions {
        Predicate<Utilizator> isNull = Objects::isNull;
        if (isNull.test(entity)){
            throw new RepositoryExceptions("Entitatea nu poate sa fie null.");
        }
        validator.validate(entity);
        Optional<Utilizator> result = Optional.empty();
        String updateSqlStatement = "update users set first_name=?, last_name=? where id=?";
        try (Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
             PreparedStatement updateStatement = connection.prepareStatement(updateSqlStatement, Statement.RETURN_GENERATED_KEYS)
        ){
            updateStatement.setString(1,entity.getFirstName());
            updateStatement.setString(2,entity.getLastName());
            updateStatement.setLong(3,entity.getId());
            if (updateStatement.executeUpdate() > 0){
                ResultSet resultSet = updateStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    result = Optional.empty();
                }
                else{
                    //System.out.println("Update failed");
                    result = Optional.of(entity);
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return result;
    }
    @Override
    public Optional<Utilizator> loadFriends(Long idUser) throws RepositoryExceptions{
        Optional<Utilizator> user = findOne(idUser);
        if (user.isEmpty()){
            throw new RepositoryExceptions("Nu exista acest utilizator.");
        }
        Utilizator utilizator = user.get();
        String sqlStatement = "SELECT U.* FROM users U, friendships F WHERE" +
                "(F.userid1=? AND F.userid2=U.id) OR (F.userid2=? AND F.userid1=U.id);";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement statement = connection.prepareStatement(sqlStatement)
        ){
            statement.setLong(1,idUser);
            statement.setLong(2,idUser);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Utilizator u = new Utilizator(firstName, lastName);
                u.setId(id);
                utilizator.addFriend(u);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.of(utilizator);
    }
    @Override
    public Iterable<Prietenie> loadFriendsMonth(Long idUser, int month) throws RepositoryExceptions{
        Optional<Utilizator> user = findOne(idUser);
        if (user.isEmpty()){
            throw new RepositoryExceptions("Nu exista acest utilizator.");
        }
        ArrayList<Prietenie> result = new ArrayList<>();
        Utilizator utilizator = user.get();
        String sqlStatement = "select * from friendships F inner join users U1 on U1.id=F.userid1 inner join users U2 on U2.id=F.userid2 WHERE (U1.id=? OR U2.id=?)";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement statement = connection.prepareStatement(sqlStatement)
        ){
            statement.setLong(1,idUser);
            statement.setLong(2,idUser);
            //statement.setInt(1, month);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Utilizator u1 = new Utilizator(null, null);
                u1.setId(resultSet.getLong(4));
                u1.setFirstName(resultSet.getString(5));
                u1.setLastName(resultSet.getString(6));

                Utilizator u2 = new Utilizator(null, null);
                u2.setId(resultSet.getLong(7));
                u2.setFirstName(resultSet.getString(8));
                u2.setLastName(resultSet.getString(9));

                Tuple<Long,Long> id = new Tuple<>(resultSet.getLong(1),resultSet.getLong(2));
                Prietenie p = new Prietenie(u1,u2);
                p.setId(id);
                p.setDate(resultSet.getTimestamp(3).toLocalDateTime());

                result.add(p);
                //String firstName = resultSet.getString("first_name");
                //String lastName = resultSet.getString("last_name");
                //Utilizator u = new Utilizator(firstName, lastName);
                //u.setId(id);
                //utilizator.addFriend(u);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        //Iterable<String> newResult = result.stream()
          //      .filter(friendship -> friendship.getDate().getMonthValue() == month)
            //    .map(friendship -> friendship.getUser1() + " | " + friendship.getUser2() + " | " + friendship.getDate())
              //  .toList();

        return result.stream()
                .filter(u->u.getDate().getMonthValue() == month)
                .toList();



    }
}
