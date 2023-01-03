package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class User {
    @JsonAlias({"id"})
    UUID id;
    @JsonAlias({"password"})
    String password;
    @JsonAlias({"username"})
    String username;
    @JsonAlias({"name"})
    String name;
    @JsonAlias({"bio"})
    String bio;
    @JsonAlias({"image"})
    String image;
    @JsonAlias({"role_id"})
    int role_id;
    @JsonAlias({"elo"})
    int elo;
    @JsonAlias({"games_played"})
    int games_played;
    @JsonAlias({"games_won"})
    int games_won;
    @JsonAlias({"coins"})
    int coins;

    // Jackson needs the default constructor
    public User() {}
}
