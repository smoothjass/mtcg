package app.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CardDTO {
    @JsonAlias({"id"})
    UUID id;
    @JsonAlias({"damage"})
    int damage;
    @JsonAlias({"cardtype"})
    String cardtype;
    @JsonAlias({"element"})
    String element;
    @JsonAlias({"username"})
    String username;
    @JsonAlias({"used_in_deck"})
    boolean used_in_deck;
    @JsonAlias({"used_in_trade"})
    boolean used_in_trade;
}
