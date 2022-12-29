package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class City {
    // A Model has only properties and no methods
    // except for getters, and in some cases setters
    // you would set the values with an all args constructor
    // A Model represents an Object that can usually map
    // 1:1 onto a database table

    @JsonAlias({"id"})
    Integer id;
    @JsonAlias({"name"})
    String name;
    @JsonAlias({"population"})
    int population;

    // Jackson needs the default constructor
    public City() {}
}
