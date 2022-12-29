package app.daos;

import app.models.City;
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
public class CityDao implements Dao<City, Integer> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    // Source: https://www.baeldung.com/java-dao-vs-repository
    // A DAO is an abstraction of data persistence.
    // However, a repository is an abstraction of a collection of objects
    // A DAO is a lower-level concept, closer to the storage systems.
    // However, Repository is a higher-level concept, closer to the Domain objects

    // We need to inject the connection so we can mock that object
    // for easier testing
    public CityDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public City create(City city) throws SQLException {
        // always use prepared statements
        // e.g. prepare the values with question marks (?)
        String query = "INSERT INTO cities (name, population) VALUES (?, ?)";


        // the string gets send to db
        // db now knows the where the values have to go
        PreparedStatement stmt = getConnection().prepareStatement(query);
        // now we tell the db what the values for those placeholders are
        // important: we need to tell the db the data type as well
        // e.g. string, int, etc...
        stmt.setString(1, city.getName());
        stmt.setInt(2, city.getPopulation());
        // after the db has the prepared statement and the values for the
        // placeholders we tell the db it should execute the statement with
        // the values we provided
        ResultSet result = stmt.executeQuery();

        // as a result we get back an ResultSet
        // we can use it to access the results returned from the db
        // e.g.
        // id, name, population
        // 1, "Graz", 250000
        // we can access the first colum by using the index 1
        // index starts at 1
        // result.getInt(1); => 1
        // result.getString(2); => "Graz"
        // result.getInt(3); => 250000
        // we need to tell what we expect the data type to be
        // e.g. getInt, getString, etc...
        // if a resultSet returns multiple rows we can iterate over it with
        // while (result.next()) {}
        City createdCity = new City(
            result.getInt(1),
            result.getString(2),
            result.getInt(3)
        );

        return createdCity;
    }

    @Override
    public HashMap<Integer, City> read() throws SQLException {
        // we create a hashmap
        // "Key" => "Value"
        // e.g.
        // {
        //   "1": { "id": 1, "name": "Graz", "population": 250000 },
        //   "2": { "id": 2, "name": "Vienna", "population": 1900000 },
        // }
        // a hashmap has different methods like every object
        // e.g. put("Key", "Value") to add a new entry to it
        // with a hashmap we have the ability to access a value
        // at O(1) when we know the key to a value
        // otherwise if we use an arraylist we would need to
        // iterate over all items until we found the value we need
        HashMap<Integer, City> cities = new HashMap();
        // SQL Statement
        String query = "SELECT * FROM cities";
        // prepare the statement
        // we do not have any user input here, so it would not be necessary
        // but we do it anyway
        PreparedStatement stmt = getConnection().prepareStatement(query);

        // execute the statement
        ResultSet result = stmt.executeQuery();

        // iterate over the result set
        while (result.next()) {
            City city = new City(
                result.getInt(1),
                result.getString(2),
                result.getInt(3)
            );

            // add the city to the hashmap
            cities.put(city.getId(), city);
        }

        // return the hashmap that will be used in the DAO
        return cities;
    }

    @Override
    public void update() throws SQLException {

    }

    @Override
    public void delete() throws SQLException {

    }
}
