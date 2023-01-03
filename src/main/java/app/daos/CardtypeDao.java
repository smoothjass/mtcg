package app.daos;

import app.models.Card;
import app.models.Cardtype;
import app.models.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class CardtypeDao implements Dao<Cardtype, Integer> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;
    public CardtypeDao(Connection connection) { setConnection(connection); }

    @Override
    public Cardtype create(Cardtype cardtype) throws SQLException {
        return null;
    }

    @Override
    public HashMap<Integer, Cardtype> read() throws SQLException {
        HashMap<Integer, Cardtype> cardtypes = new HashMap();
        String query = "SELECT * FROM cardtypes";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        while (result.next()) {
            Cardtype cardtype = new Cardtype(
                    result.getInt(1),
                    result.getString(2),
                    result.getBoolean(3)
            );
            cardtypes.put(cardtype.getId(), cardtype);
        }
        return cardtypes;
    }

    @Override
    public Cardtype update(String s, Cardtype cardtype) throws SQLException {
        return null;
    }

    @Override
    public void delete() throws SQLException {

    }
}
