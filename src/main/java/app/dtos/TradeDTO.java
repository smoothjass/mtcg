package app.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TradeDTO {
    @JsonAlias({"id"})
    UUID id;
    @JsonAlias({"card_id"})
    UUID card_id;
    @JsonAlias({"cardtype"})
    String cardtype;
    @JsonAlias({"element"})
    String element;
    @JsonAlias({"min_damage"})
    int min_damage;
}
