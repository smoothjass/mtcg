package app.daos;

import app.models.Cardtype;

import java.sql.SQLException;
import java.util.HashMap;

public class CardtypeDao implements Dao<Cardtype, Integer> {
    @Override
    public Cardtype create(Cardtype cardtype) throws SQLException {
        return null;
    }

    @Override
    public HashMap<Integer, Cardtype> read() throws SQLException {
        return null;
    }

    @Override
    public Cardtype update(String s, Cardtype cardtype) throws SQLException {
        return null;
    }

    @Override
    public void delete() throws SQLException {

    }
}
