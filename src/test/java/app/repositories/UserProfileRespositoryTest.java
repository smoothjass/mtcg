package app.repositories;

import app.daos.RoleDao;
import app.daos.UserDao;
import app.dtos.UserProfileDTO;
import app.models.Role;
import app.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserProfileRespositoryTest {
    @Test
    @DisplayName("Test: userProfileRespository getAll() expect 3 users")
    public void getAll_expect3Users() {
        try {
            // arrange
            RoleDao roleDaoMock = mock(RoleDao.class);
            HashMap<Integer, Role> roles = new HashMap<>();
            roles.put(0, new Role(0, "admin"));
            roles.put(1, new Role(1, "user"));
            when(roleDaoMock.read()).thenReturn(roles);

            UserDao userDaoMock = mock(UserDao.class);
            HashMap<UUID, User> dbUsers = new HashMap<>();
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            when(userDaoMock.read()).thenReturn(dbUsers);

            UserProfileRepository userProfileRepository = new UserProfileRepository(userDaoMock, roleDaoMock);

            // act
            ArrayList<UserProfileDTO> userProfiles = userProfileRepository.getAll();

            // assert
            assertEquals(3, userProfiles.size());
            assertEquals(3, userProfileRepository.getUserProfilesCache().size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @DisplayName("Test: userProfileRespository getByUsername expect null")
    public void getByUsername_ExpectNull() {
        try {
            // arrange
            RoleDao roleDaoMock = mock(RoleDao.class);
            HashMap<Integer, Role> roles = new HashMap<>();
            roles.put(0, new Role(0, "admin"));
            roles.put(1, new Role(1, "user"));
            when(roleDaoMock.read()).thenReturn(roles);

            UserDao userDaoMock = mock(UserDao.class);
            HashMap<UUID, User> dbUsers = new HashMap<>();
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            when(userDaoMock.read()).thenReturn(dbUsers);

            UserProfileRepository userProfileRepository = new UserProfileRepository(userDaoMock, roleDaoMock);
            userProfileRepository.getUserProfilesCache().clear();

            // act
            UserProfileDTO user = userProfileRepository.getByUsername("kienboec");

            // assert
            assertNull(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }@Test
    @DisplayName("Test: userProfileRespository getByUsername expect kienboec")
    public void getAll_expect1UserKienboec() {
        try {
            // arrange
            RoleDao roleDaoMock = mock(RoleDao.class);
            HashMap<Integer, Role> roles = new HashMap<>();
            roles.put(0, new Role(0, "admin"));
            roles.put(1, new Role(1, "user"));
            when(roleDaoMock.read()).thenReturn(roles);

            UserDao userDaoMock = mock(UserDao.class);
            HashMap<UUID, User> dbUsers = new HashMap<>();
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "kienboec", null, null, null, 1, 100, 0, 0, 0, 20));
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            when(userDaoMock.read()).thenReturn(dbUsers);

            UserProfileRepository userProfileRepository = new UserProfileRepository(userDaoMock, roleDaoMock);
            userProfileRepository.getUserProfilesCache().clear();

            // act
            UserProfileDTO user = userProfileRepository.getByUsername("kienboec");

            // assert
            assertNotEquals(null, user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }@Test
    @DisplayName("Test: userProfileRespository postUser altenhof")
    public void getAll_expectUserProfileDTOforAltenhof() {
        try {
            // arrange
            RoleDao roleDaoMock = mock(RoleDao.class);
            HashMap<Integer, Role> roles = new HashMap<>();
            roles.put(0, new Role(0, "admin"));
            roles.put(1, new Role(1, "user"));
            when(roleDaoMock.read()).thenReturn(roles);

            UserDao userDaoMock = mock(UserDao.class);
            HashMap<UUID, User> dbUsers = new HashMap<>();
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            dbUsers.put(UUID.randomUUID(), new User(UUID.randomUUID(), "bla", "bla", null, null, null, 1, 100, 0, 0, 0, 20));
            when(userDaoMock.read()).thenReturn(dbUsers);

            UserProfileRepository userProfileRepository = new UserProfileRepository(userDaoMock, roleDaoMock);
            userProfileRepository.getUserProfilesCache().clear();

            User user = new User(UUID.randomUUID(), "bla", "altenhof", null, null, null, 1, 100, 0, 0, 0, 20);
            when(userDaoMock.create(any())).thenReturn(user);
            // act
            UserProfileDTO newUser = userProfileRepository.postUser(user);

            // assert
            assertNotEquals(null, newUser);
            assertEquals(4, userProfileRepository.getUserProfilesCache().size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
