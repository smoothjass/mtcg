package app.dtos;

import app.models.Card;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class PackageDTO {
    @JsonAlias({"id"})
    Integer id;
    @JsonAlias({"cards"})
    ArrayList<CardDTO> cards;

    public PackageDTO(){}
}
