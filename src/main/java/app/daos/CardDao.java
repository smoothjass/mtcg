package app.daos;

import app.models.Card;
import app.models.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.xml.transform.Result;
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
        String insert = "INSERT INTO cards (card_id, damage, cardtype_id, element_id, package_id) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement stmt1 = getConnection().prepareStatement(insert);
        stmt1.setObject(1, card.getId());
        stmt1.setInt(2, card.getDamage());
        stmt1.setInt(3, card.getCardtype_id());
        stmt1.setInt(4, card.getElement_id());
        stmt1.setObject(5, card.getPackage_id());
        int insertResult = stmt1.executeUpdate();
        String query = "SELECT * from cards where card_id = ?";
        PreparedStatement stmt2 = getConnection().prepareStatement(query);
        stmt2.setObject(1, card.getId());
        ResultSet result = stmt2.executeQuery();
        if (result.next()) {
            Card newCard = new Card(
                    (UUID) result.getObject(1),
                    result.getInt(2),
                    result.getInt(3),
                    result.getInt(4),
                    (UUID) result.getObject(5),
                    (UUID) result.getObject(6),
                    result.getBoolean(7),
                    result.getBoolean(8)
            );
            return newCard;
        }
        return null;
    }

    @Override
    public HashMap<UUID, Card> read() throws SQLException {
        HashMap<UUID, Card> cards = new HashMap();
        String query = "SELECT * FROM cards;";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        while (result.next()) {
            Card card = new Card(
                    (UUID) result.getObject(1),
                    result.getInt(2),
                    result.getInt(3),
                    result.getInt(4),
                    (UUID) result.getObject(5),
                    (UUID) result.getObject(6),
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
