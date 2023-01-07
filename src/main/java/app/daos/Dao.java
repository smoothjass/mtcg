package app.daos;

import app.models.User;

import java.sql.SQLException;
import java.util.HashMap;

// Dao needs two Types to work
// first the object and second the ID
public interface Dao<T, ID> {
    // we add the CRUD methods on the interface
    // that needs to be implemented by the DAOs
    T create(T t) throws SQLException;
    HashMap<ID, T> read() throws SQLException;
    // you could also return the newly updated or
    // deleted object, but
    // we already have all the information on the client
    // we want to reduce complexity
    T update(String s, T t) throws SQLException;
    void delete(T t) throws SQLException;
}
