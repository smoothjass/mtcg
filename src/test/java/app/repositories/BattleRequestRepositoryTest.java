package app.repositories;

import app.daos.BattleRequestDao;
import app.models.BattleRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import static org.mockito.Mockito.*;

public class BattleRequestRepositoryTest {
    @Test
    @DisplayName("Test: battleRequestRepository getAll() when cache is empty")
    public void getAll_expectEmptyHashmap() {
        try {
            // arrange
            BattleRequestDao battleRequestDaoMock = mock(BattleRequestDao.class);
            BattleRequestRepository battleRequestRepository = new BattleRequestRepository(battleRequestDaoMock);
            when(battleRequestDaoMock.read()).thenReturn(new LinkedHashMap<String, BattleRequest>());

            // act
            LinkedHashMap<String, BattleRequest> actualMap = battleRequestRepository.getAll();

            // assert
            assertEquals(0, actualMap.size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @DisplayName("Test: battleRequestRepository getAll() when cache is empty and \"db\" has entry altenhof")
    public void getAll_expectHashmapWithEntry() {
        try {
            // arrange
            BattleRequestDao battleRequestDaoMock = mock(BattleRequestDao.class);
            BattleRequestRepository battleRequestRepository = new BattleRequestRepository(battleRequestDaoMock);
            LinkedHashMap<String, BattleRequest> dbEntries = new LinkedHashMap<String, BattleRequest>();
            dbEntries.put("altenhof", new BattleRequest(1, "altenhof"));
            when(battleRequestDaoMock.read()).thenReturn(dbEntries);
            battleRequestRepository.getBattleRequestsCache().clear();

            // act
            LinkedHashMap<String, BattleRequest> actualMap = battleRequestRepository.getAll();

            // assert
            assertEquals(1, actualMap.size());
            assertNull(actualMap.get("kienboec"));
            assertNotEquals(null, actualMap.get("altenhof"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @DisplayName("Test: battleRequestRepository getAll() when cache has entry kienboec")
    public void getAll_expectCache() {
        try {
            // arrange
            BattleRequestDao battleRequestDaoMock = mock(BattleRequestDao.class);
            BattleRequestRepository battleRequestRepository = new BattleRequestRepository(battleRequestDaoMock);
            when(battleRequestDaoMock.read()).thenReturn(new LinkedHashMap<String, BattleRequest>());
            battleRequestRepository.getBattleRequestsCache().put("kienboec", new BattleRequest(1, "kienboec"));

            // act
            LinkedHashMap<String, BattleRequest> actualMap = battleRequestRepository.getAll();

            // assert
            assertEquals(1, actualMap.size());
            assertNull(actualMap.get("altenhof"));
            assertNotEquals(null, actualMap.get("kienboec"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test: battleRequestRepository postRequest() add request to empty cache")
    public void postRequest_expectToAddToCache() {
        try {
            //arrange
            BattleRequestDao battleRequestDaoMock = mock(BattleRequestDao.class);
            BattleRequestRepository battleRequestRepository = new BattleRequestRepository(battleRequestDaoMock);
            BattleRequest newRequest = new BattleRequest("kienboec");
            when(battleRequestDaoMock.create(newRequest)).thenReturn(new BattleRequest(1, "kienboec"));
            battleRequestRepository.getBattleRequestsCache().clear();

            // act
            battleRequestRepository.postBattleRequest(newRequest);

            // assert
            assertEquals(1, battleRequestRepository.getBattleRequestsCache().size());
            assertNotEquals(null, battleRequestRepository.getBattleRequestsCache().get("kienboec"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test: battleRequestRepository deleteRequest() remove request from cache")
    public void deleteRequest_expectToDeleteFromCache() {
        //arrange
        BattleRequestDao battleRequestDaoMock = mock(BattleRequestDao.class);
        BattleRequestRepository battleRequestRepository = new BattleRequestRepository(battleRequestDaoMock);
        BattleRequest oldRequest = new BattleRequest("kienboec");
        battleRequestRepository.getBattleRequestsCache().put("kienboec", oldRequest);

        // act
        battleRequestRepository.deleteRequest(oldRequest);

        // assert
        assertEquals(0, battleRequestRepository.getBattleRequestsCache().size());
        assertEquals(null, battleRequestRepository.getBattleRequestsCache().get("kienboec"));
    }
}
