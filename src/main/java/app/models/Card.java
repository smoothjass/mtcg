package app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Card {
    @JsonAlias({"id"})
    UUID id;
    @JsonAlias({"damage"})
    int damage;
    @JsonAlias({"cardtype_id"})
    int cardtype_id;
    @JsonAlias({"element_id"})
    int element_id;
    @JsonAlias({"user_id"})
    UUID user_id;
    @JsonAlias({"package_id"})
    int package_id;
    @JsonAlias({"used_in_deck"})
    boolean used_in_deck;
    @JsonAlias({"used_in_trade"})
    boolean used_in_trade;
}
