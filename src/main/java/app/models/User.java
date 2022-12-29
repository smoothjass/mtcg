package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    // See City Model
    @JsonAlias({"id"})
    Integer id;
    @JsonAlias({"name"})
    String name;
    @JsonAlias({"cityId"})
    int cityId;

    // Jackson needs the default constructor
    public User() {}
}
