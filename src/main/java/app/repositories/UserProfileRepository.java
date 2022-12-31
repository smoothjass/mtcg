package app.repositories;

import app.daos.CityDao;
import app.daos.UserDao;
import app.dtos.UserProfileDTO;
import app.models.City;
import app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class UserProfileRepository implements Repository<UserProfileDTO, Integer> {
    // Source: https://www.baeldung.com/java-dao-vs-repository
    // A DAO is an abstraction of data persistence.
    // However, a repository is an abstraction of a collection of objects
    // A DAO is a lower-level concept, closer to the storage systems.
    // However, Repository is a higher-level concept, closer to the Domain objects
    UserDao userDao;
    CityDao cityDao;
    // hashmap takes a key and a value
    // Key will be the id of the user (Integer)
    // value would be the userProfile
    HashMap<UUID, UserProfileDTO> userProfilesCache = new HashMap();
    // {
    //   key: value,
    //   uuid: new UserProfileDTO(),
    //   uuid: new UserProfileDTO(),
    //
    // }

    // we inject the DAOs
    // with that in mind we can test this class
    // because we can inject mocked DAO objects
    public UserProfileRepository(UserDao userDao, CityDao cityDao) {
        setUserDao(userDao);
        setCityDao(cityDao);
    }

    @Override
    public ArrayList<UserProfileDTO> getAll() {
        try {
            System.out.println(getUserProfilesCache().isEmpty());
            if (!getUserProfilesCache().isEmpty()) {
                // when we use a hashmap for our cache we need to transform
                // the hashmap into an arraylist of the values in our hashmap
                return new ArrayList(userProfilesCache.values());
            }

            // get all cities
            HashMap<Integer, City> cities = getCityDao().read();
            // get all users
            HashMap<Integer, User> users = getUserDao().read();

            // combine
            // With Iterator
            Iterator<User> userIterator = new ArrayList(users.values()).iterator();
            // we still need to iterate over all users
            // It could be more readable with:
            // for (User user: new ArrayList<>(users.values())) {}

            while (userIterator.hasNext()) {
                User user = userIterator.next();

                // we do not need to iterate over all
                // cities since we can access a city by Id
                // and we have the cityId on the user Object
                // we can access a city in O(1)
/*
//das gehört eigentlich entkommentiert, aber ich hab das DTO geändert und mich damit noch nicht befasst

                City city = cities.get(user.getCityId());

                UserProfileDTO userProfile = new UserProfileDTO(
                    user.getId(),
                    user.getName(),
                    city
                );

                userProfilesCache.put(userProfile.getId(), userProfile);
*/
                // We do not want to iterate over all cities per user
                // so, we implement a hashmap for our cities store
                // otherwise our implementation would look like this
                /*
                for (City city : cities) {
                    if (user.getCityId() == city.getId()) {
                        UserProfileDTO userProfile = new UserProfileDTO(
                            user.getId(),
                            user.getName(),
                            city
                        );
                        userProfilesCache.put(userProfile.getId(), userProfile);
                    }
                }
                 */
            }

            return new ArrayList(userProfilesCache.values());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UserProfileDTO getById(Integer ID) {
        System.out.println(getUserProfilesCache().isEmpty());
        if (!getUserProfilesCache().isEmpty()) {
            // when we use a hashmap for our cache we
            // can access the user profile by O(1)
            UserProfileDTO userProfileDTO = userProfilesCache.get(ID);
            // we would need to check if this variable is empty and
            // check if its existent in our database
            return userProfileDTO;
        }

        // we could refetch all
        getAll();
        // or we can simply fetch only one item
        // but, we would need to implement that in our dao
        // as well as in our interface
        // User user = getUserDao().read(ID)

        UserProfileDTO userProfileDTO = userProfilesCache.get(ID);

        return userProfileDTO;
    }
    public UserProfileDTO postUser(User data) {
        try {
            // TODO first check somewhere of username exists already, probably with getAll
            getUserDao().create(data);
            UserProfileDTO userProfile = new UserProfileDTO(
                    //stuff hineintun
            );
            return userProfile;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
