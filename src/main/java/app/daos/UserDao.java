package app.daos;

import app.dtos.UserProfileDTO;
import app.models.User;
import com.google.common.hash.Hashing;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

// The City Data Access Object implements the DAO interface
// we tell the interface that our Type (T) will be a City
// and our Type (ID) will be an Integer
// See City Dao for details
public class UserDao implements Dao<User, UUID> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public UserDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public User create(User user) throws SQLException {
        String update = "INSERT INTO users (user_id, password, username) VALUES(?,?,?);";
        PreparedStatement stmt1 = getConnection().prepareStatement(update);
        stmt1.setObject(1, UUID.randomUUID());
        String sha256hex = Hashing.sha256()
                .hashString(user.getPassword(), StandardCharsets.UTF_8)
                .toString();
        stmt1.setString(2, sha256hex);
        stmt1.setString(3, user.getUsername());
        // System.out.println(stmt);
        int insert = stmt1.executeUpdate();
        // System.out.println(result);
        String query = "SELECT * FROM users WHERE username = ?";
        PreparedStatement stmt2 = getConnection().prepareStatement(query);
        stmt2.setString(1, user.getUsername());
        ResultSet result = stmt2.executeQuery();
        if (result.next()){
            User newUser = new User(
                    (UUID) result.getObject(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getInt(7),
                    result.getInt(8),
                    result.getInt(9),
                    result.getInt(10),
                    result.getInt(11),
                    result.getInt(12)
            );
            return newUser;
        }
        return null;
    }

    @Override
    public HashMap<UUID, User> read() throws SQLException {
        HashMap<UUID, User> users = new HashMap();
        String query = "SELECT * FROM users";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        ResultSet result = stmt.executeQuery();

        while (result.next()) {
            User user = new User(
                    (UUID) result.getObject(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getInt(7),
                    result.getInt(8),
                    result.getInt(9),
                    result.getInt(10),
                    result.getInt(11),
                    result.getInt(12)
            );

            users.put(user.getId(), user);
        }
        return users;
    }

    // overload read if specific order is needed
    public ArrayList<User> read(String orderBy) throws SQLException {
        ArrayList<User> users = new ArrayList<User>();
        String query = "SELECT * FROM users ORDER BY ? DESC";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        stmt.setString(1, orderBy);
        ResultSet result = stmt.executeQuery();

        while (result.next()) {
            User user = new User(
                    (UUID) result.getObject(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getInt(7),
                    result.getInt(8),
                    result.getInt(9),
                    result.getInt(10),
                    result.getInt(11),
                    result.getInt(12)
            );
            System.out.println(user.getUsername() + user.getElo());
            users.add(user);
        }
        return users;
    }

    @Override
    public User update(String username, User user) throws SQLException {
        String update = "UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?";
        PreparedStatement stmt = getConnection().prepareStatement(update);
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getBio());
        stmt.setString(3, user.getImage());
        stmt.setString(4, username);
        int updateResult = stmt.executeUpdate();

        String query = "SELECT * FROM users WHERE username = ?";
        PreparedStatement stmt2 = getConnection().prepareStatement(query);
        stmt2.setString(1, username);
        ResultSet result = stmt2.executeQuery();
        if (result.next()) {
            User newUser = new User(
                    (UUID) result.getObject(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6),
                    result.getInt(7),
                    result.getInt(8),
                    result.getInt(9),
                    result.getInt(10),
                    result.getInt(11),
                    result.getInt(12)
            );
            return newUser;
        }
        return null;
    }

    @Override
    public void delete(User user) throws SQLException {

    }

    public Integer update(String username) throws SQLException {
        String update = "UPDATE users SET coins = (coins-5) WHERE username = ?;";
        PreparedStatement stmt = getConnection().prepareStatement(update);
        stmt.setString(1, username);
        int updateResult = stmt.executeUpdate();

        String query = "SELECT coins FROM users WHERE username = ?;";
        PreparedStatement stmt1 = getConnection().prepareStatement(query);
        stmt1.setString(1, username);
        ResultSet result = stmt1.executeQuery();
        if (result.next()) {
            return result.getInt(1);
        }
        return -1;
    }

    public ArrayList<User> update(UserProfileDTO winningPlayer, UserProfileDTO losingPlayer, boolean draw) throws SQLException {
        String update;
        if (draw) {
            update = "UPDATE users " +
                    "SET games_played = (games_played+1) " +
                    "WHERE username = ? OR username = ?;";
        }
        else{
            update = "UPDATE users SET games_played = (games_played+1), " +
                    "games_won = (games_won+1), " +
                    "elo = (elo+3)" +
                    "WHERE username = ?;" +
                    "UPDATE users SET games_played = (games_played+1), " +
                    "games_lost = (games_lost+1), " +
                    "elo = (elo-5)" +
                    "WHERE username = ?;";
        }
        PreparedStatement stmt = getConnection().prepareStatement(update);
        stmt.setString(1, winningPlayer.getUsername());
        stmt.setString(2, losingPlayer.getUsername());
        int updateResult = stmt.executeUpdate();

        String query = "SELECT * from users WHERE username = ? OR username = ?;";
        PreparedStatement stmt1 = getConnection().prepareStatement(query);
        stmt1.setString(1, winningPlayer.getUsername());
        stmt1.setString(2, losingPlayer.getUsername());
        ResultSet result = stmt1.executeQuery();
        ArrayList<User> users = new ArrayList<>();
        while(result.next()) {
            User user = new User(
                (UUID) result.getObject(1),
                result.getString(2),
                result.getString(3),
                result.getString(4),
                result.getString(5),
                result.getString(6),
                result.getInt(7),
                result.getInt(8),
                result.getInt(9),
                result.getInt(10),
                result.getInt(11),
                result.getInt(12)
            );
            users.add(user);
        }
        return users;
    }
}
