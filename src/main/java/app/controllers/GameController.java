package app.controllers;

import app.dtos.CardDTO;
import app.dtos.UserProfileDTO;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class GameController extends Controller{
    CardRepository cardRepository;
    UserProfileRepository userProfileRepository;
    BattleRequestRepository battleRequestRepository;
    HashMap<String, Integer> elements = new HashMap<String, Integer>();
    float[][] effectiveness = new float[3][3];
    String gameLog = "";

    public GameController(CardRepository cardRepository, UserProfileRepository userProfileRepository, BattleRequestRepository battleRequestRepository) {
        setCardRepository(cardRepository);
        setUserProfileRepository(userProfileRepository);
        setBattleRequestRepository(battleRequestRepository);

        // TODO if time allows it: this should actually get the elements from DB so they are always up to date when a new element is introduced to the game
        elements.put("fire", 0);
        elements.put("water", 1);
        elements.put("normal", 2);

        effectiveness[0][0] = 1;
        effectiveness[0][1] = 0.5F;
        effectiveness[0][2] = 2;
        effectiveness[1][0] = 2;
        effectiveness[1][1] = 1;
        effectiveness[1][2] = 0.5F;
        effectiveness[2][0] = 0.5F;
        effectiveness[2][1] = 2;
        effectiveness[2][2] = 1;
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
                setGameLog("");
                this.wait();

                // game was carried out but stats couldn't be written
                if (Objects.equals(getGameLog(), "")) {
                    return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{ \"data\": null, \"error\": internal server error } "
                    );
                }

                // all is good
                return new Response(
                    HttpStatus.OK,
                    ContentType.TEXT,
                    getGameLog()
                );
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            // a user cannot fight themselves
            if (battleRequests.get(username) != null) {
                return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": battle request for this user exists already }"
                );
            }
            // get a battlerequest from the cache and delete it from db and cache
            BattleRequest battleRequest = (BattleRequest) battleRequests.values().toArray()[0];
            getBattleRequestRepository().deleteRequest(battleRequest);

            // carry out the battle
            String content = battle(battleRequest.getUsername(), username);
            setGameLog(content);
            this.notify();

            // game was carried out but stats couldn't be written to db for some yet unknown reason:
            if (Objects.equals(getGameLog(), "")) {
                return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": internal server error } "
                );
            }

            // all is good
            return new Response(
                HttpStatus.OK,
                ContentType.TEXT,
                content
            );
        }
    }

    protected String battle(String requestingPlayer, String acceptingPlayer) {
        // get players
        UserProfileDTO player1 = getUserProfileRepository().getByUsername(requestingPlayer);
        UserProfileDTO player2 = getUserProfileRepository().getByUsername(acceptingPlayer);

        // get their decks
        ArrayList<CardDTO> player1Deck = getCardRepository().getForUser(player1.getId());
        player1Deck.removeIf(card -> !card.isUsed_in_deck());
        ArrayList<CardDTO> player2Deck = getCardRepository().getForUser(player2.getId());
        player2Deck.removeIf(card -> !card.isUsed_in_deck());

        StringBuilder log = new StringBuilder("begin of play: \n" +
                "player 1: " + player1.getUsername() +
                "\ndeck:\n" +
                player1Deck.get(0).getElement() + " " + player1Deck.get(0).getCardtype() + " " + player1Deck.get(0).getDamage() + "\n" +
                player1Deck.get(1).getElement() + " " + player1Deck.get(1).getCardtype() + " " + player1Deck.get(1).getDamage() + "\n" +
                player1Deck.get(2).getElement() + " " + player1Deck.get(2).getCardtype() + " " + player1Deck.get(2).getDamage() + "\n" +
                player1Deck.get(3).getElement() + " " + player1Deck.get(3).getCardtype() + " " + player1Deck.get(3).getDamage() + "\n" +
                "player 2: " + player2.getUsername() +
                "\ndeck:\n" +
                player2Deck.get(0).getElement() + " " + player2Deck.get(0).getCardtype() + " " + player2Deck.get(0).getDamage() + "\n" +
                player2Deck.get(1).getElement() + " " + player2Deck.get(1).getCardtype() + " " + player2Deck.get(1).getDamage() + "\n" +
                player2Deck.get(2).getElement() + " " + player2Deck.get(2).getCardtype() + " " + player2Deck.get(2).getDamage() + "\n" +
                player2Deck.get(3).getElement() + " " + player2Deck.get(3).getCardtype() + " " + player2Deck.get(3).getDamage() + "\n");

        int winner = 0;
        for(int i = 0; i < 100; ++i){
            if(player1Deck.isEmpty()){
                winner = 2;
                break;
            }
            if(player2Deck.isEmpty()){
                winner = 1;
                break;
            }
            int index1 = (int)(Math.random() * player1Deck.size());
            int index2 = (int)(Math.random() * player2Deck.size());
            // 0 -> draw, 1 -> player 1 wins round, 2 -> player 2 wins round
            winner = compare(player1Deck.get(index1), player2Deck.get(index2));
            String round = "";
            switch (winner) {
                case 0:
                    log.append("round ").append(Integer.toString(i+1)).append(": draw\n");
                    break;
                case 1:
                    round =
                        player1.getUsername() + "'s " + player1Deck.get(index1).getElement() + " " + player1Deck.get(index1).getCardtype() + " defeated " +
                        player2.getUsername() + "'s " + player2Deck.get(index2).getElement() + " " + player2Deck.get(index2).getCardtype() + "\n";
                    log.append("round ").append(Integer.toString(i+1)).append(": ").append(round);
                    player1Deck.add(player2Deck.get(index2));
                    player2Deck.remove(index2);
                    break;
                case 2:
                    round =
                        player2.getUsername() + "'s " + player2Deck.get(index2).getElement() + " " + player2Deck.get(index2).getCardtype() + " defeated " +
                        player1.getUsername() + "'s " + player1Deck.get(index1).getElement() + " " + player1Deck.get(index1).getCardtype() + "\n";
                    log.append("round ").append(Integer.toString(i+1)).append(": ").append(round);
                    player2Deck.add(player1Deck.get(index1));
                    player1Deck.remove(index1);
                    break;
            }
        }
        Integer gameStatsUpdate = 0;
        if(player1Deck.size() > player2Deck.size()) {
            log.append(player1.getUsername()).append(" won\n");
            gameStatsUpdate = getUserProfileRepository().updateUser(player1, player2, false);
        }
        if(player2Deck.size() > player1Deck.size()) {
            log.append(player2.getUsername()).append(" won\n");
            gameStatsUpdate = getUserProfileRepository().updateUser(player2, player1, false);
        }
        if(player1Deck.size() == player2Deck.size()) {
            log.append("game result: draw\n");
            gameStatsUpdate = getUserProfileRepository().updateUser(player1, player2, true);
        }
        if (gameStatsUpdate < 0 ) {
            return "";
        }
        for(CardDTO card: player1Deck) {
            if (card.getUser_id() != player1.getId()) {
                getCardRepository().updateCard(player1.getId(), card);
            }
        }
        for(CardDTO card: player2Deck) {
            if (card.getUser_id() != player2.getId()) {
                getCardRepository().updateCard(player2.getId(), card);
            }
        }
        getCardRepository().updateCardCache();
        return log.toString();
    }

    private int compare(CardDTO player1Card, CardDTO player2Card) {
        // pure monster fight
        if (!Objects.equals(player1Card.getCardtype(), "spell") && !Objects.equals(player2Card.getCardtype(), "spell")) {
            // Goblins are too afraid of Dragons to attack
            if (Objects.equals(player1Card.getCardtype(), "goblin") && Objects.equals(player2Card.getCardtype(), "dragon")) {
                return 2;
            }
            if (Objects.equals(player2Card.getCardtype(), "goblin") && Objects.equals(player1Card.getCardtype(), "dragon")) {
                return 1;
            }

            // Wizzard can control Orks so they are not able to damage them
            if (Objects.equals(player1Card.getCardtype(), "wizzard") && Objects.equals(player2Card.getCardtype(), "ork")) {
                return 1;
            }
            if (Objects.equals(player1Card.getCardtype(), "ork") && Objects.equals(player2Card.getCardtype(), "wizzard")) {
                return 2;
            }

            // The FireElves know Dragons since they were little and can evade their attacks
            if (Objects.equals(player1Card.getCardtype(), "elf") &&
                    Objects.equals(player1Card.getElement(), "fire") &&
                        Objects.equals(player2Card.getCardtype(), "dragon")) {
                return 1;
            }
            if (Objects.equals(player2Card.getCardtype(), "elf") &&
                    Objects.equals(player2Card.getElement(), "fire") &&
                        Objects.equals(player1Card.getCardtype(), "dragon")) {
                return 2;
            }
        }

        // The armor of Knights is so heavy that WaterSpells make them drown them instantly
        if (Objects.equals(player2Card.getCardtype(), "spell") &&
                Objects.equals(player2Card.getElement(), "water") &&
                Objects.equals(player1Card.getCardtype(), "knight")) {
            return 2;
        }
        if (Objects.equals(player1Card.getCardtype(), "spell") &&
                Objects.equals(player1Card.getElement(), "water") &&
                Objects.equals(player2Card.getCardtype(), "knight")) {
            return 1;
        }

        // The Kraken is immune against spells
        if (Objects.equals(player1Card.getCardtype(), "kraken") && Objects.equals(player2Card.getCardtype(), "spell")) {
            return 1;
        }
        if (Objects.equals(player2Card.getCardtype(), "kraken") && Objects.equals(player1Card.getCardtype(), "spell")) {
            return 2;
        }

        // calculate damage based on damage and element for all other cases
        float damage1 = player1Card.getDamage();
        float damage2 = player2Card.getDamage();
        String element1 = player1Card.getElement();
        String element2 = player2Card.getElement();

        damage1 = damage1 * effectiveness[elements.get(element1)][elements.get(element2)];
        damage2 = damage2 * effectiveness[elements.get(element2)][elements.get(element1)];
        if (damage1 - damage2 > 0) {
            return 1;
        }
        if (damage1 - damage2 < 0) {
            return 2;
        }
        else {
            return 0;
        }
    }
}
