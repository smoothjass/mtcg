package app.dtos;

import app.models.City;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
    Integer id;
    @JsonAlias({"name"})
    String name;
    @JsonAlias({"city"})
    City city;

    // Jackson needs the default constructor
    public UserProfileDTO() {}
}
