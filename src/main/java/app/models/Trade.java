package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Trade {
    @JsonAlias({"id"})
    UUID id;
    @JsonAlias({"card_id"})
    UUID card_id;
    @JsonAlias({"cardtype_id"})
    int cardtype_id;
    @JsonAlias({"element_id"})
    int element_id;
    @JsonAlias({"min_damage"})
    int min_damage;

    public Trade() {}
}
