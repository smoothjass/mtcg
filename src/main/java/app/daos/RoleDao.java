package app.daos;

import app.models.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class RoleDao implements Dao<Role, Integer>{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public RoleDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public Role create(Role role) throws SQLException {
        return null;
    }

    @Override
    public HashMap<Integer, Role> read() throws SQLException {
        HashMap<Integer, Role> roles = new HashMap();
        String query = "SELECT * FROM roles";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        while (result.next()) {
            Role role = new Role(
                    result.getInt(1),
                    result.getString(2)
            );
            roles.put(role.getId(), role);
        }
        return roles;
    }

    @Override
    public Role update(String description, Role role) throws SQLException {
        return null;
    }

    @Override
    public void delete() throws SQLException {

    }
}
