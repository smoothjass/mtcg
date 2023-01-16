package app.controllers;

import app.dtos.CardDTO;
import app.dtos.UserProfileDTO;
import app.models.BattleRequest;
import app.repositories.BattleRequestRepository;
import app.repositories.CardRepository;
import app.repositories.UserProfileRepository;
import http.ContentType;
import http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

// nun beim testen merke ich, dass es vermutlich smart gewesen wäre, die battle und compare
// Funktionen aus dem Controller hinaus zu lagern, damit die Methoden mehr isoliert und damit
// leichter zu testen sind.
// Außerdem wäre ein anderer return value für battle() auch sinnvoller um die results zu überprüfen
// Lessons Learned!

// die synchronized method zu testen habe ich probiert, es ist aber recht umständlich und ich habe
// wieder das Gefühl, dass ich dabei eher die mocks teste, als die Methode selbst.

public class GameControllerTest {
    @Test
    @DisplayName("Test: Game Controller: battle expect draw (both have four of the same card in their decks)")
    public void battle_expectDraw() {
        // arrange
        CardRepository cardRepositoryMock = mock(CardRepository.class);
        UserProfileRepository userProfileRepositoryMock = mock(UserProfileRepository.class);
        BattleRequestRepository battleRequestRepositoryMock = mock(BattleRequestRepository.class);

        UserProfileDTO testUser = new UserProfileDTO(
                UUID.randomUUID(), "bla", "bla", "bla",
                null, null, null,
                0, 0, 0, 0, 0);
        when(userProfileRepositoryMock.getByUsername(any())).thenReturn(testUser);

        CardDTO cardInDeck = new CardDTO(
                UUID.randomUUID(), 10, "spell", "water", UUID.randomUUID(),
                null, true, false);
        CardDTO cardNotInDeck = new CardDTO(
                UUID.randomUUID(), 20, "spell", "water", UUID.randomUUID(),
                null, false, false);
        ArrayList<CardDTO> cards = new ArrayList<>();
        cards.add(cardInDeck);
        cards.add(cardInDeck);
        cards.add(cardInDeck);
        cards.add(cardInDeck);
        cards.add(cardNotInDeck); // will be removed
        when(cardRepositoryMock.getForUser(any())).thenReturn(cards);

        GameController gameController = new GameController(
                cardRepositoryMock, userProfileRepositoryMock, battleRequestRepositoryMock);

        // act
        String actualContent = gameController.battle("user1", "user2");
        String lines[] = actualContent.split("\n");
        String lastLine = lines[lines.length-1];

        assertEquals("game result: draw", lastLine);
    }

    @Test
    @DisplayName("Test: Game Controller: battle expect altenhof won")
    public void battle_expectAltenhofWon() {
        // arrange
        CardRepository cardRepositoryMock = mock(CardRepository.class);
        UserProfileRepository userProfileRepositoryMock = mock(UserProfileRepository.class);
        BattleRequestRepository battleRequestRepositoryMock = mock(BattleRequestRepository.class);

        UUID altenhofId = UUID.randomUUID();
        UserProfileDTO altenhof = new UserProfileDTO(
                altenhofId, "bla", "altenhof", "bla",
                null, null, null,
                0, 0, 0, 0, 0);
        when(userProfileRepositoryMock.getByUsername("altenhof")).thenReturn(altenhof);

        UUID kienboecId = UUID.randomUUID();
        UserProfileDTO kienboec = new UserProfileDTO(
                kienboecId, "bla", "kienboec", "bla",
                null, null, null,
                0, 0, 0, 0, 0);
        when(userProfileRepositoryMock.getByUsername("kienboec")).thenReturn(kienboec);

        CardDTO cardInDeck = new CardDTO(
                UUID.randomUUID(), 20, "spell", "water", UUID.randomUUID(),
                null, true, false);
        CardDTO cardNotInDeck = new CardDTO(
                UUID.randomUUID(), 20, "spell", "water", UUID.randomUUID(),
                null, false, false);
        ArrayList<CardDTO> cards = new ArrayList<>();
        cards.add(cardInDeck);
        cards.add(cardInDeck);
        cards.add(cardInDeck);
        cards.add(cardInDeck);
        cards.add(cardNotInDeck); // will be removed
        when(cardRepositoryMock.getForUser(altenhofId)).thenReturn(cards);

        CardDTO cardInDeckWeak = new CardDTO(
                UUID.randomUUID(), 10, "spell", "water", UUID.randomUUID(),
                null, true, false);
        ArrayList<CardDTO> cardsWeak = new ArrayList<>();
        cardsWeak.add(cardInDeckWeak);
        cardsWeak.add(cardInDeckWeak);
        cardsWeak.add(cardInDeckWeak);
        cardsWeak.add(cardInDeckWeak);
        cardsWeak.add(cardNotInDeck); // will be removed
        when(cardRepositoryMock.getForUser(kienboecId)).thenReturn(cardsWeak);

        GameController gameController = new GameController(
                cardRepositoryMock, userProfileRepositoryMock, battleRequestRepositoryMock);

        // act
        String actualContent = gameController.battle("altenhof", "kienboec");
        String lines[] = actualContent.split("\n");
        String lastLine = lines[lines.length-1];

        assertEquals("altenhof won", lastLine);
    }
}
