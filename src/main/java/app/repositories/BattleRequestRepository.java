package app.repositories;

import app.daos.BattleRequestDao;
import app.models.BattleRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.LinkedHashMap;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PROTECTED)
public class BattleRequestRepository {
    BattleRequestDao battleRequestDao;
    LinkedHashMap<String, BattleRequest> battleRequestsCache = new LinkedHashMap<>();

    public BattleRequestRepository(BattleRequestDao battleRequestDao) {
        setBattleRequestDao(battleRequestDao);
    }

    public LinkedHashMap<String, BattleRequest> getAll() {
        if (!battleRequestsCache.isEmpty()) {
            return battleRequestsCache;
        }
        try {
            return getBattleRequestDao().read();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void postBattleRequest(BattleRequest battleRequest) {
        try {
            BattleRequest newRequest = getBattleRequestDao().create(battleRequest);
            getBattleRequestsCache().put(newRequest.getUsername(), newRequest);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRequest(BattleRequest battleRequest) {
        try {
            getBattleRequestDao().delete(battleRequest);
            getBattleRequestsCache().remove(battleRequest.getUsername());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
