package app.repositories;

import app.daos.CityDao;
import app.daos.RoleDao;
import app.daos.UserDao;
import app.dtos.UserProfileDTO;
import app.models.City;
import app.models.Role;
import app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.*;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class UserProfileRepository implements Repository<UserProfileDTO, Integer> {
    // Source: https://www.baeldung.com/java-dao-vs-repository
    // A DAO is an abstraction of data persistence.
    // However, a repository is an abstraction of a collection of objects
    // A DAO is a lower-level concept, closer to the storage systems.
    // However, Repository is a higher-level concept, closer to the Domain objects
    UserDao userDao;
    RoleDao roleDao;
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
    public UserProfileRepository(UserDao userDao, RoleDao roleDao) {
        setUserDao(userDao);
        setRoleDao(roleDao);
    }

    @Override
    public ArrayList<UserProfileDTO> getAll() {
        try {
            // System.out.println(getUserProfilesCache().isEmpty());
            if (!getUserProfilesCache().isEmpty()) {
                // when we use a hashmap for our cache we need to transform
                // the hashmap into an arraylist of the values in our hashmap
                return new ArrayList(userProfilesCache.values());
            }
            // get roles
            HashMap<Integer, Role> roles = getRoleDao().read();
            // get all users
            HashMap<UUID, User> users = getUserDao().read();
            for (User user: new ArrayList<>(users.values())) {
                UserProfileDTO userProfile = new UserProfileDTO(
                        user.getId(),
                        user.getPassword(),
                        user.getUsername(),
                        roles.get(user.getRole_id()).getName(),
                        user.getElo(),
                        user.getGames_played(),
                        user.getGames_won(),
                        user.getCoins()
                );
                userProfilesCache.put(userProfile.getId(), userProfile);
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
            if(getByUsername(data.getUsername()) == null){
                getUserDao().create(data);
                UserProfileDTO userProfile = new UserProfileDTO(
                        //stuff hineintun
                );
                return userProfile;
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserProfileDTO getByUsername(String username) {
        if (getUserProfilesCache().isEmpty()) {
            getAll();
        }
        for (UserProfileDTO user: new ArrayList<>(userProfilesCache.values())) {
            // System.out.println(user);
            if (Objects.equals(user.getUsername(), username)) {
                return user;
            }
        }
        return null;
    }

}
