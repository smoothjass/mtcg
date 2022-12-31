package app.daos;

import app.models.City;
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
                    result.getInt(4),
                    result.getInt(5),
                    result.getInt(6),
                    result.getInt(7),
                    result.getInt(8)
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
                    result.getInt(4),
                    result.getInt(5),
                    result.getInt(6),
                    result.getInt(7),
                    result.getInt(8)
            );

            users.put(user.getId(), user);
        }
        return users;
    }

    @Override
    public void update() throws SQLException {

    }

    @Override
    public void delete() throws SQLException {

    }
}
