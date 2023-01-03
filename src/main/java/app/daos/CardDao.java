package app.daos;

import app.models.Card;
import app.models.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class CardDao implements Dao<Card, UUID>{
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;
    public CardDao(Connection connection) { setConnection(connection); }

    @Override
    public Card create(Card card) throws SQLException {
        return null;
    }

    @Override
    public HashMap<UUID, Card> read() throws SQLException {
        HashMap<UUID, Card> cards = new HashMap();
        String query = "SELECT * FROM cards";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        while (result.next()) {
            Card card = new Card(
                    (UUID) result.getObject(1),
                    result.getInt(2),
                    result.getInt(3),
                    result.getInt(4),
                    (UUID) result.getObject(5),
                    result.getInt(6),
                    result.getBoolean(7),
                    result.getBoolean(8)
            );
            cards.put(card.getId(), card);
        }
        return cards;
    }

    @Override
    public Card update(String s, Card card) throws SQLException {
        return null;
    }

    @Override
    public void delete() throws SQLException {

    }
}
