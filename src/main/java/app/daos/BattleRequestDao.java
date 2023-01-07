package app.daos;

import app.models.BattleRequest;
import app.repositories.BattleRequestRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BattleRequestDao implements Dao<BattleRequest, String>{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;
    public BattleRequestDao(Connection connection) { setConnection(connection); }

    @Override
    public BattleRequest create(BattleRequest battleRequest) throws SQLException {
        String insert = "INSERT INTO battlerequests (username) VALUES (?);";
        PreparedStatement stmt = getConnection().prepareStatement(insert);
        stmt.setString(1, battleRequest.getUsername());
        int insertResult = stmt.executeUpdate();

        String query = "SELECT * from battlerequests WHERE username = ?;";
        PreparedStatement stmt1 = getConnection().prepareStatement(query);
        stmt1.setString(1, battleRequest.getUsername());
        ResultSet result = stmt1.executeQuery();

        if (result.next()) {
            BattleRequest newRequest = new BattleRequest(
                result.getInt(1),
                result.getString(2)
            );
            return newRequest;
        }
        return null;
    }

    @Override
    public LinkedHashMap<String, BattleRequest> read() throws SQLException {
        String query = "SELECT * FROM battlerequests;";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        LinkedHashMap<String, BattleRequest> battleRequests = new LinkedHashMap<>();
        while (result.next()) {
            BattleRequest battleRequest = new BattleRequest(
                result.getInt(1),
                result.getString(2)
            );
            battleRequests.put(battleRequest.getUsername(), battleRequest);
        }
        return battleRequests;
    }

    @Override
    public BattleRequest update(String s, BattleRequest battleRequest) throws SQLException {
        return null;
    }

    public void delete(BattleRequest battleRequest) throws SQLException {
        String delete = "DELETE FROM battlerequests WHERE username = ?;";
        PreparedStatement stmt = getConnection().prepareStatement(delete);
        stmt.setString(1, battleRequest.getUsername());
        int deleteResult = stmt.executeUpdate();
    }
}
