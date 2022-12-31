package app.dtos;

import app.models.City;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileDTO {
    // DTO => Data Transfer Object
    // https://www.baeldung.com/java-dto-pattern

    // A DTO only has properties but should not have any methods
    // except for getters, and in some cases setters
    // you would set the values with an all args constructor
    // A DTO is used to transfer data between domains
    // so if we need user to have a city we can mix it here

    // we need to set JSON aliases so that jackson can
    // safely parse it
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
    @JsonAlias({"role"})
    String role;
    @JsonAlias({"elo"})
    int elo;
    @JsonAlias({"games_played"})
    int games_played;
    @JsonAlias({"games_won"})
    int games_won;
    @JsonAlias({"coins"})
    int coins;

    // Jackson needs the default constructor
    public UserProfileDTO() {}
}
