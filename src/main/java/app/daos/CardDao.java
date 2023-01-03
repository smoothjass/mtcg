package app.daos;

import app.models.Card;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class CardDao implements Dao<Card, UUID>{
    @Override
    public Card create(Card card) throws SQLException {
        return null;
    }

    @Override
    public HashMap<UUID, Card> read() throws SQLException {
        return null;
    }

    @Override
    public Card update(String s, Card card) throws SQLException {
        return null;
    }

    @Override
    public void delete() throws SQLException {

    }
}
