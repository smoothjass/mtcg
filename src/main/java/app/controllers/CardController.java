package app.controllers;

import app.dtos.CardDTO;
import app.dtos.PackageDTO;
import app.dtos.UserProfileDTO;
import app.repositories.CardRepository;
import app.repositories.UserProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class CardController extends Controller {
    CardRepository cardRepository;
    UserProfileRepository userProfileRepository;

    public CardController(CardRepository cardRepository, UserProfileRepository userProfileRepository) {
        setCardRepository(cardRepository);
        setUserProfileRepository(userProfileRepository);
    }

    public Response createPackage(String body) {
        try {
            ArrayList tempCards = getObjectMapper().readValue(body, ArrayList.class);
            ArrayList<CardDTO> newCards = new ArrayList<CardDTO>();
            for(Object card: tempCards) {
                newCards.add(getObjectMapper().readValue(getObjectMapper().writeValueAsString(card), CardDTO.class));
            }
            PackageDTO newPackage = getCardRepository().postPackage(newCards);
            if (newPackage != null){
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

    public Response acquirePackage(String username) {
        UserProfileDTO user = getUserProfileRepository().getByUsername(username);
        if (user == null) {
            return new Response(
                HttpStatus.UNAUTHORIZED,
                ContentType.JSON,
                "{\"data\": null, \"error\": Access token is missing or invalid } "
            );
        }
        if (user.getCoins() < 5) {
            return new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{\"data\": null, \"error\": Not enough money for buying a card package } "
            );
        }
        PackageDTO acquiredpackage = getCardRepository().acquirePackage(user);
        if (acquiredpackage == null) {
            return new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{\"data\": null, \"error\": No card package available for buying } "
            );
        }
        try {
            // TODO reduce user's coins
            String packageJSON = getObjectMapper().writeValueAsString(acquiredpackage);
            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{\"description\": a package has been successfully bought, \"data\": "+ packageJSON + ", \"error\": null } "
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Response getCardsForUser(String username, boolean deckRequested, boolean plainFormat) {
        UserProfileDTO user = getUserProfileRepository().getByUsername(username);
        if (user == null) {
            return new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{\"data\": null, \"error\": User does not exist } "
            );
        }
        ArrayList<CardDTO> cards = getCardRepository().getForUser(user.getId());
        if (deckRequested) {
            cards.removeIf(card -> !card.isUsed_in_deck());
        }
        String content = "";
        if (cards.isEmpty()) {
            if (deckRequested) {
                content = "{ \"description\": the deck doesn't have any cards, \"data\": null, \"error\": null } ";
            }
            else {
                content = "{ \"description\": the user doesn't have any cards, \"data\": null, \"error\": null } ";
            }
            return new Response(
                HttpStatus.NO_CONTENT,
                ContentType.JSON,
                content
            );
        }
        try {
            String cardsJSON = getObjectMapper().writeValueAsString(cards);
            if (deckRequested) {
                content = "{\"description\": the deck has cards, \"data\": "+ cardsJSON + ", \"error\": null } ";
                if (plainFormat) { // TODO string schöner machen
                    content = cardsJSON.toString();
                    return new Response(
                        HttpStatus.OK,
                        ContentType.TEXT,
                        content
                    );
                }
            }
            else{
                content = "{\"description\": the user has cards, \"data\": "+ cardsJSON + ", \"error\": null } ";
            }
            return new Response( // TODO JSON schöner machen
                HttpStatus.OK,
                ContentType.JSON,
                content
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Response configureDeck(String username, String body) {
        try {
            ArrayList deckCards = getObjectMapper().readValue(body, ArrayList.class);
            if (deckCards.size() != 4) {
                return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{\"data\": null, \"error\": The provided deck did not include the required amount of cards } "
                );
            }
            UserProfileDTO user = getUserProfileRepository().getByUsername(username);
            if (user == null) {
                return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{\"data\": null, \"error\": User does not exist } "
                );
            }
            ArrayList<CardDTO> cards = getCardRepository().getForUser(user.getId());
            cards.removeIf(CardDTO::isUsed_in_trade);
            cards.removeIf(card -> !deckCards.contains(card.getId().toString()));

            if (cards.size() != 4){
                return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{\"data\": null, \"error\": At least one of the provided cards does not belong to the user or is not available } "
                );
            }
            getCardRepository().configureDeck(cards);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"description\": the deck has been successfully configured, \"data\": null, \"error\": null } "
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
