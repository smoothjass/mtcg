package app.daos;

import app.models.Element;

import java.sql.SQLException;
import java.util.HashMap;

public class ElementDao implements Dao<Element, Integer> {
    @Override
    public Element create(Element element) throws SQLException {
        return null;
    }

    @Override
    public HashMap<Integer, Element> read() throws SQLException {
        return null;
    }

    @Override
    public Element update(String s, Element element) throws SQLException {
        return null;
    }

    @Override
    public void delete() throws SQLException {

    }
}
