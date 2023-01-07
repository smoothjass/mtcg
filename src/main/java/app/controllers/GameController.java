package app.controllers;

import app.models.BattleRequest;
import app.repositories.BattleRequestRepository;
import app.repositories.CardRepository;
import app.repositories.UserProfileRepository;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class GameController extends Controller{
    CardRepository cardRepository;
    UserProfileRepository userProfileRepository;
    BattleRequestRepository battleRequestRepository;
    public GameController(CardRepository cardRepository, UserProfileRepository userProfileRepository, BattleRequestRepository battleRequestRepository) {
        setCardRepository(cardRepository);
        setUserProfileRepository(userProfileRepository);
        setBattleRequestRepository(battleRequestRepository);
    }


    public synchronized Response enterLobby(String username) {
        LinkedHashMap<String, BattleRequest> battleRequests = getBattleRequestRepository().getAll();
        if (battleRequests.isEmpty()) {
            // persist battle request to DB and wait to be accepted
            System.out.println("Waiting for battle request to be accepted...");
            try {
                BattleRequest battleRequest = new BattleRequest(username);
                // System.out.println(battleRequest.getBattle_id() + battleRequest.getUsername());
                getBattleRequestRepository().postBattleRequest(battleRequest);
                this.wait();
                return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"description\": the battle has been carried out successfully, \"error\": null }"
                );
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            // this should never happen if i get around to implement proper session handling with
            // user state management but i might run out of time and in that case
            // i don't want a user to be able to fight themselves
            if (battleRequests.get(username) != null) {
                return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": battle request for this user exists already }"
                );
            }
            // battle, persist battle data in DB, delete battle request from DB, notify
            System.out.println("battle, persist battle data in DB, delete battle request from DB");
            // TODO continue here battle game logic, persist game outcome to db
            BattleRequest battleRequest = (BattleRequest) battleRequests.values().toArray()[0];
            getBattleRequestRepository().deleteRequest(battleRequest);

            this.notify();
            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"description\": the battle has been carried out successfully, \"error\": null }"
            );
        }
    }
}
