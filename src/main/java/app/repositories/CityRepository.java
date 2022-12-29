package app.repositories;

import app.daos.CityDao;
import app.models.City;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

// See UserProfileRepository for details

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class CityRepository {
    CityDao cityDao;
    HashMap<Integer, City> citiesCache = new HashMap<>();

    //
    CityRepository(CityDao cityDao) {
        setCityDao(cityDao);
    }

    public ArrayList<City> getAll() {
        if (!citiesCache.isEmpty()) {
            // as we need to return an arraylist
            // we need to convert the values of the hashmap to an arraylist
            return new ArrayList<>(getCitiesCache().values());
        }

        try {
            HashMap<Integer, City> cities = getCityDao().read();

            setCitiesCache(cities);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(getCitiesCache().values());
    }
}
