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

import java.util.ArrayList;

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
        PackageDTO acquiredPackge = getCardRepository().acquirePackage(user);
        if (acquiredPackge == null) {
            return new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{\"data\": null, \"error\": No card package available for buying } "
            );
        }
        try {
            // TODO reduce user's coins
            String packageJSON = getObjectMapper().writeValueAsString(acquiredPackge);
            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{\"description\": a package has been successfully bought, \"data\": "+ packageJSON + ", \"error\": null } "
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
