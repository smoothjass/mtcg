package app.daos;

import app.models.Element;
import app.models.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ElementDao implements Dao<Element, Integer> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;
    public ElementDao(Connection connection) { setConnection(connection); }

    @Override
    public Element create(Element element) throws SQLException {
        return null;
    }

    @Override
    public HashMap<Integer, Element> read() throws SQLException {
        HashMap<Integer, Element> elements = new HashMap();
        String query = "SELECT * FROM elements";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        while (result.next()) {
            Element element = new Element(
                    result.getInt(1),
                    result.getString(2)
            );
            elements.put(element.getId(), element);
        }
        return elements;
    }

    @Override
    public Element update(String s, Element element) throws SQLException {
        return null;
    }

    @Override
    public void delete() throws SQLException {

    }
}
