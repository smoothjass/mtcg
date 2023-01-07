package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Cardtype {
    @JsonAlias({"id"})
    int id;
    @JsonAlias({"name"})
    String name;
    @JsonAlias({"monster"})
    boolean monster;

    public Cardtype() {}
}
