package app.daos;

import app.models.Trade;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class TradeDao implements Dao<Trade, UUID>{
    @Override
    public Trade create(Trade trade) throws SQLException {
        return null;
    }

    @Override
    public HashMap<UUID, Trade> read() throws SQLException {
        return null;
    }

    @Override
    public Trade update(String s, Trade trade) throws SQLException {
        return null;
    }

    @Override
    public void delete() throws SQLException {

    }
}
