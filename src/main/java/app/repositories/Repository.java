package app.repositories;

import java.util.ArrayList;

public interface Repository<T, ID> {
    ArrayList<T> getAll();
    T getById(ID id);
}
