package app.repositories;

import app.daos.RoleDao;
import app.daos.UserDao;
import app.dtos.UserProfileDTO;
import app.models.Role;
import app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

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
                        user.getName(),
                        user.getBio(),
                        user.getImage(),
                        roles.get(user.getRole_id()).getName(),
                        user.getElo(),
                        user.getGames_played(),
                        user.getGames_won(),
                        user.getGames_lost(),
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

    // overload get all if sorting is needed
    public ArrayList<UserProfileDTO> getAll(String orderBy) {
        try {
            ArrayList<UserProfileDTO> userProfiles = new ArrayList<UserProfileDTO>();
            HashMap<Integer, Role> roles = getRoleDao().read();
            ArrayList<User> users = getUserDao().read(orderBy);
            for (User user: users) {
                System.out.println(user.getUsername() + user.getElo());
                UserProfileDTO userProfile = new UserProfileDTO(
                        user.getId(),
                        user.getPassword(),
                        user.getUsername(),
                        user.getName(),
                        user.getBio(),
                        user.getImage(),
                        roles.get(user.getRole_id()).getName(),
                        user.getElo(),
                        user.getGames_played(),
                        user.getGames_won(),
                        user.getGames_lost(),
                        user.getCoins()
                );
                userProfiles.add(userProfile);
            }
            return userProfiles;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserProfileDTO getById(Integer ID) {
        // delete later
        return null;
    }

    public UserProfileDTO postUser(User data) {
        try {
            if(getByUsername(data.getUsername()) == null){
                HashMap<Integer, Role> roles = getRoleDao().read();
                User user = getUserDao().create(data);
                UserProfileDTO userProfile = new UserProfileDTO(
                        user.getId(),
                        user.getPassword(),
                        user.getUsername(),
                        user.getName(),
                        user.getBio(),
                        user.getImage(),
                        roles.get(user.getRole_id()).getName(),
                        user.getElo(),
                        user.getGames_played(),
                        user.getGames_won(),
                        user.getGames_lost(),
                        user.getCoins()
                );
                userProfilesCache.put(userProfile.getId(), userProfile);
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
            // System.out.println("cache empty, gettin all");
            getAll();
        }
        for (UserProfileDTO user: new ArrayList<>(userProfilesCache.values())) {
            // System.out.println(user.getUsername());
            if (Objects.equals(user.getUsername(), username)) {
                return user;
            }
        }
        return null;
    }

    public UserProfileDTO updateUser(String username, User data) {
        if(getByUsername(username) != null) {
            //user exists -> do the update
            try {
                HashMap<Integer, Role> roles = getRoleDao().read();
                User user = getUserDao().update(username, data);
                UserProfileDTO userProfile = new UserProfileDTO(
                        user.getId(),
                        user.getPassword(),
                        user.getUsername(),
                        user.getName(),
                        user.getBio(),
                        user.getImage(),
                        roles.get(user.getRole_id()).getName(),
                        user.getElo(),
                        user.getGames_played(),
                        user.getGames_won(),
                        user.getGames_lost(),
                        user.getCoins()
                );
                userProfilesCache.put(userProfile.getId(), userProfile);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        // now either null because no such user, or updated dto
        return getByUsername(username);
    }

    public Integer reduceCoinsFor(UserProfileDTO user) {
        Integer coins = null;
        try {
            coins = getUserDao().update(user.getUsername());
            if (coins < 0) {
                return coins;
            }
            getUserProfilesCache().get(user.getId()).setCoins(coins);
            return coins;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer updateUser(UserProfileDTO winningPlayer, UserProfileDTO losingPlayer, boolean draw) {
        try {
            ArrayList<User> updatedUsers = null;
            updatedUsers = getUserDao().update(winningPlayer, losingPlayer, draw);
            if (updatedUsers.isEmpty()) {
                // in case we mysteriously couldn't write to db but did not throw sql exception
                return -1;
            }
            for(User user: updatedUsers) {
                getUserProfilesCache().get(user.getId()).setElo(user.getElo());
                getUserProfilesCache().get(user.getId()).setGames_played(user.getGames_played());
                getUserProfilesCache().get(user.getId()).setGames_won(user.getGames_won());
                getUserProfilesCache().get(user.getId()).setGames_lost(user.getGames_lost());
            }
            return 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
