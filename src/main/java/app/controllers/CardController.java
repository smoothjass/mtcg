package app.controllers;

import app.dtos.CardDTO;
import app.dtos.PackageDTO;
import app.repositories.CardRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.ArrayList;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class CardController extends Controller {
    CardRepository cardRepository;

    public CardController(CardRepository cardRepository) {
        setCardRepository(cardRepository);
    }

    public Response createPackage(String body) {
        try {
            ArrayList tempCards = getObjectMapper().readValue(body, ArrayList.class);
            ArrayList<CardDTO> cards = new ArrayList<CardDTO>();
            for(Object card: tempCards) {
                cards.add(getObjectMapper().readValue(getObjectMapper().writeValueAsString(card), CardDTO.class));
            }
            PackageDTO newPackage = getCardRepository().postPackage(cards);
            if (newPackage != null){
                // TODO add to cache
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{\"description\": package and cards successfully created, \"data\": null, \"error\": null }"
                );
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new Response(
                HttpStatus.CONFLICT,
                ContentType.JSON,
                "{\"data\": null, \"error\": At least one card in the packages already exists }"
        );
    }
}
