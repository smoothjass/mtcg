package app.daos;

import app.models.Package;

import java.sql.SQLException;
import java.util.HashMap;

public class PackageDao implements Dao<Package, Integer> {
    @Override
    public Package create(Package aPackage) throws SQLException {
        return null;
    }

    @Override
    public HashMap<Integer, Package> read() throws SQLException {
        return null;
    }

    @Override
    public Package update(String s, Package aPackage) throws SQLException {
        return null;
    }

    @Override
    public void delete() throws SQLException {

    }
}
