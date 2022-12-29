package app.daos;

import app.models.City;
import app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

// The City Data Access Object implements the DAO interface
// we tell the interface that our Type (T) will be a City
// and our Type (ID) will be an Integer
// See City Dao for details
public class UserDao implements Dao<User, Integer> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public UserDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public User create(User user) throws SQLException {
        return null;
    }

    @Override
    public HashMap<Integer, User> read() throws SQLException {
        HashMap<Integer, User> users = new HashMap();
        String query = "SELECT * FROM users";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        ResultSet result = stmt.executeQuery();

        while (result.next()) {
            User user = new User(
                result.getInt(1),
                result.getString(2),
                result.getInt(3)
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
