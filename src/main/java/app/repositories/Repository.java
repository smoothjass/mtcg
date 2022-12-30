package app.repositories;

import app.dtos.UserProfileDTO;
import app.models.User;

import java.util.ArrayList;

public interface Repository<T, ID> {
    ArrayList<T> getAll();
    T getById(ID id);
}
