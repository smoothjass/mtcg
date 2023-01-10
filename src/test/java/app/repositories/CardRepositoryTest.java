package app.repositories;

import app.daos.CardDao;
import app.daos.CardtypeDao;
import app.daos.ElementDao;
import app.dtos.CardDTO;
import app.dtos.PackageDTO;
import app.dtos.UserProfileDTO;
import app.models.Card;
import app.models.Cardtype;
import app.models.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class CardRepositoryTest {
    @Test
    @DisplayName("Test: update cards cache from 1 to 3 cards")
    public void updateCardCache_expectHashmapOfSize3() {
        try {
            // arrange
            CardDao cardDaoMock = mock(CardDao.class);
            CardtypeDao cardtypeDaoMock = mock(CardtypeDao.class);
            ElementDao elementDaoMock = mock(ElementDao.class);

            HashMap<Integer, Cardtype> cardtypes = new HashMap<>();
            cardtypes.put(1, new Cardtype(1, "spell", false));
            cardtypes.put(2, new Cardtype(2, "goblin", true));
            when(cardtypeDaoMock.read()).thenReturn(cardtypes);

            HashMap<Integer, Element> elements = new HashMap<>();
            elements.put(1, new Element(1, "fire"));
            elements.put(2, new Element(2, "water"));
            elements.put(3, new Element(3, "normal"));
            when(elementDaoMock.read()).thenReturn(elements);

            CardRepository cardRepository = new CardRepository(cardDaoMock, cardtypeDaoMock, elementDaoMock);

            cardRepository.getCardCache().put(UUID.randomUUID(), new CardDTO(UUID.randomUUID(), 0, "spell", "water", null, UUID.randomUUID(), false, false));
            HashMap<UUID, Card> dbCards = new HashMap<>();
            dbCards.put(UUID.randomUUID(), new Card(UUID.randomUUID(), 0, 2, 2, null, UUID.randomUUID(), false, false));
            dbCards.put(UUID.randomUUID(), new Card(UUID.randomUUID(), 0, 2, 2, null, UUID.randomUUID(), false, false));
            dbCards.put(UUID.randomUUID(), new Card(UUID.randomUUID(), 0, 2, 2, null, UUID.randomUUID(), false, false));

            when(cardDaoMock.read()).thenReturn(dbCards);

            // act
            cardRepository.updateCardCache();

            // assert
            cardRepository.getCardCache().forEach((key, value) -> {
                assertEquals(value.getCardtype(), "goblin");
            });
            assertEquals(3, cardRepository.getCardCache().size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test: getAll() with non emppty cache")
    public void getAllWithNonEmptyCache_ExpectHashMapSize1() {
        try {
            // arrange
            CardDao cardDaoMock = mock(CardDao.class);
            CardtypeDao cardtypeDaoMock = mock(CardtypeDao.class);
            ElementDao elementDaoMock = mock(ElementDao.class);

            HashMap<Integer, Cardtype> cardtypes = new HashMap<>();
            cardtypes.put(1, new Cardtype(1, "spell", false));
            cardtypes.put(2, new Cardtype(2, "goblin", true));
            when(cardtypeDaoMock.read()).thenReturn(cardtypes);

            HashMap<Integer, Element> elements = new HashMap<>();
            elements.put(1, new Element(1, "fire"));
            elements.put(2, new Element(2, "water"));
            elements.put(3, new Element(3, "normal"));
            when(elementDaoMock.read()).thenReturn(elements);

            CardRepository cardRepository = new CardRepository(cardDaoMock, cardtypeDaoMock, elementDaoMock);

            cardRepository.getCardCache().put(UUID.randomUUID(), new CardDTO(UUID.randomUUID(), 0, "spell", "water", null, UUID.randomUUID(), false, false));

            when(cardDaoMock.read()).thenReturn(new HashMap<>());

            // act
            cardRepository.getAll();

            // assert
            cardRepository.getCardCache().forEach((key, value) -> {
                assertEquals(value.getCardtype(), "spell");
            });
            assertEquals(1, cardRepository.getCardCache().size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test: getAll() with empty cache expect Hashmap of size 3")
    public void getAllWithEmptyCacheExpectHashMapOfSize3() {
        try {
            // arrange
            CardDao cardDaoMock = mock(CardDao.class);
            CardtypeDao cardtypeDaoMock = mock(CardtypeDao.class);
            ElementDao elementDaoMock = mock(ElementDao.class);

            HashMap<Integer, Cardtype> cardtypes = new HashMap<>();
            cardtypes.put(1, new Cardtype(1, "spell", false));
            cardtypes.put(2, new Cardtype(2, "goblin", true));
            when(cardtypeDaoMock.read()).thenReturn(cardtypes);

            HashMap<Integer, Element> elements = new HashMap<>();
            elements.put(1, new Element(1, "fire"));
            elements.put(2, new Element(2, "water"));
            elements.put(3, new Element(3, "normal"));
            when(elementDaoMock.read()).thenReturn(elements);

            CardRepository cardRepository = new CardRepository(cardDaoMock, cardtypeDaoMock, elementDaoMock);
            cardRepository.getCardCache().clear();

            HashMap<UUID, Card> dbCards = new HashMap<>();
            dbCards.put(UUID.randomUUID(), new Card(UUID.randomUUID(), 0, 2, 2, null, UUID.randomUUID(), false, false));
            dbCards.put(UUID.randomUUID(), new Card(UUID.randomUUID(), 0, 2, 2, null, UUID.randomUUID(), false, false));
            dbCards.put(UUID.randomUUID(), new Card(UUID.randomUUID(), 0, 2, 2, null, UUID.randomUUID(), false, false));

            when(cardDaoMock.read()).thenReturn(dbCards);

            // act
            cardRepository.getAll();

            // assert
            cardRepository.getCardCache().forEach((key, value) -> {
                assertEquals(value.getCardtype(), "goblin");
            });
            assertEquals(3, cardRepository.getCardCache().size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test: cardRepository postPackage expect null because card exists already")
    public void postPackage_expectNull() {
        try {
            // arrange
            CardDao cardDaoMock = mock(CardDao.class);
            CardtypeDao cardtypeDaoMock = mock(CardtypeDao.class);
            ElementDao elementDaoMock = mock(ElementDao.class);

            HashMap<Integer, Cardtype> cardtypes = new HashMap<>();
            cardtypes.put(1, new Cardtype(1, "spell", false));
            cardtypes.put(2, new Cardtype(2, "goblin", true));
            when(cardtypeDaoMock.read()).thenReturn(cardtypes);

            HashMap<Integer, Element> elements = new HashMap<>();
            elements.put(1, new Element(1, "fire"));
            elements.put(2, new Element(2, "water"));
            elements.put(3, new Element(3, "normal"));
            when(elementDaoMock.read()).thenReturn(elements);

            CardRepository cardRepository = new CardRepository(cardDaoMock, cardtypeDaoMock, elementDaoMock);
            UUID id = UUID.randomUUID();
            cardRepository.getCardCache().put(id, new CardDTO(id, 0, "spell", "water", null, UUID.randomUUID(), false, false));

            ArrayList<CardDTO> cards = new ArrayList<>();
            cards.add(new CardDTO(id, 0, "spell", "water", null, UUID.randomUUID(), false, false));

            // act
            PackageDTO pack = cardRepository.postPackage(cards);

            // assert
            assertNull(pack);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test: postPackage expect packageDTO != null")
    public void postPackage_ExpectpackageDTONotNULL() {
        try {
            // arrange
            CardDao cardDaoMock = mock(CardDao.class);
            CardtypeDao cardtypeDaoMock = mock(CardtypeDao.class);
            ElementDao elementDaoMock = mock(ElementDao.class);

            HashMap<Integer, Cardtype> cardtypes = new HashMap<>();
            cardtypes.put(1, new Cardtype(1, "spell", false));
            cardtypes.put(2, new Cardtype(2, "goblin", true));
            when(cardtypeDaoMock.read()).thenReturn(cardtypes);

            HashMap<Integer, Element> elements = new HashMap<>();
            elements.put(1, new Element(1, "fire"));
            elements.put(2, new Element(2, "water"));
            elements.put(3, new Element(3, "normal"));
            when(elementDaoMock.read()).thenReturn(elements);

            CardRepository cardRepository = new CardRepository(cardDaoMock, cardtypeDaoMock, elementDaoMock);
            cardRepository.getCardCache().clear();
            ArrayList<CardDTO> cards = new ArrayList<>();
            UUID id = UUID.randomUUID();
            UUID packId = UUID.randomUUID();
            cards.add(new CardDTO(id, 0, "spell", "water", null, packId, false, false));

            Card card = new Card(id, 0, 1, 2, null, packId, false, false);

            when(cardDaoMock.read()).thenReturn(new HashMap<>());
            when(cardDaoMock.create(any())).thenReturn(card);

            // act
            PackageDTO pack = cardRepository.postPackage(cards);

            // assert
            cardRepository.getCardCache().forEach((key, value) -> {
                assertEquals(value.getCardtype(), "spell");
            });
            assertEquals(1, cardRepository.getCardCache().size());
            assertNotEquals(null, pack);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test: acquire package expect null because no cards available")
    public void acquirePackage_ExpectNULL() {
        try {
            // arrange
            CardDao cardDaoMock = mock(CardDao.class);
            CardtypeDao cardtypeDaoMock = mock(CardtypeDao.class);
            ElementDao elementDaoMock = mock(ElementDao.class);

            HashMap<Integer, Cardtype> cardtypes = new HashMap<>();
            cardtypes.put(1, new Cardtype(1, "spell", false));
            cardtypes.put(2, new Cardtype(2, "goblin", true));
            when(cardtypeDaoMock.read()).thenReturn(cardtypes);

            HashMap<Integer, Element> elements = new HashMap<>();
            elements.put(1, new Element(1, "fire"));
            elements.put(2, new Element(2, "water"));
            elements.put(3, new Element(3, "normal"));
            when(elementDaoMock.read()).thenReturn(elements);

            CardRepository cardRepository = new CardRepository(cardDaoMock, cardtypeDaoMock, elementDaoMock);
            cardRepository.getCardCache().clear();

            when(cardDaoMock.read()).thenReturn(new HashMap<>());

            UserProfileDTO user = mock(UserProfileDTO.class);

            // act
            PackageDTO pack = cardRepository.acquirePackage(user);

            // assert
            assertNull(pack);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test: acquire package expect package")
    public void acquirePackage_ExpectToGetPackage() {
        try {
            // arrange
            CardDao cardDaoMock = mock(CardDao.class);
            CardtypeDao cardtypeDaoMock = mock(CardtypeDao.class);
            ElementDao elementDaoMock = mock(ElementDao.class);

            HashMap<Integer, Cardtype> cardtypes = new HashMap<>();
            cardtypes.put(1, new Cardtype(1, "spell", false));
            cardtypes.put(2, new Cardtype(2, "goblin", true));
            when(cardtypeDaoMock.read()).thenReturn(cardtypes);

            HashMap<Integer, Element> elements = new HashMap<>();
            elements.put(1, new Element(1, "fire"));
            elements.put(2, new Element(2, "water"));
            elements.put(3, new Element(3, "normal"));
            when(elementDaoMock.read()).thenReturn(elements);

            CardRepository cardRepository = new CardRepository(cardDaoMock, cardtypeDaoMock, elementDaoMock);
            cardRepository.getCardCache().clear();
            UUID packId = UUID.randomUUID();
            cardRepository.getCardCache().put(UUID.randomUUID(), new CardDTO(UUID.randomUUID(), 0, "spell", "water", null, packId, false, false));
            cardRepository.getCardCache().put(UUID.randomUUID(), new CardDTO(UUID.randomUUID(), 0, "spell", "water", null, packId, false, false));

            ArrayList<Card> cards = new ArrayList<>();
            cards.add(new Card(UUID.randomUUID(), 0, 2, 2, UUID.randomUUID(), null, false, false));
            cards.add(new Card(UUID.randomUUID(), 0, 2, 2, UUID.randomUUID(), null, false, false));

            when(cardDaoMock.read()).thenReturn(new HashMap<>());
            when(cardDaoMock.update(any(), eq(packId))).thenReturn(cards);
            UserProfileDTO user = mock(UserProfileDTO.class);
            when(user.getId()).thenReturn(UUID.randomUUID());

            // act
            PackageDTO pack = cardRepository.acquirePackage(user);

            // assert
            assertNotEquals(null, pack);
            assertEquals(2, pack.getCards().size());
            for(CardDTO card: pack.getCards()){
                assertNotEquals(null, card.getUser_id());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test: get cards for user expect empty list because user has no cards")
    public void getCardsForUser_ExpectEmptyListBecauseUserHasNoCards() {
        try {
            // arrange
            CardDao cardDaoMock = mock(CardDao.class);
            CardtypeDao cardtypeDaoMock = mock(CardtypeDao.class);
            ElementDao elementDaoMock = mock(ElementDao.class);

            HashMap<Integer, Cardtype> cardtypes = new HashMap<>();
            cardtypes.put(1, new Cardtype(1, "spell", false));
            cardtypes.put(2, new Cardtype(2, "goblin", true));
            when(cardtypeDaoMock.read()).thenReturn(cardtypes);

            HashMap<Integer, Element> elements = new HashMap<>();
            elements.put(1, new Element(1, "fire"));
            elements.put(2, new Element(2, "water"));
            elements.put(3, new Element(3, "normal"));
            when(elementDaoMock.read()).thenReturn(elements);

            CardRepository cardRepository = new CardRepository(cardDaoMock, cardtypeDaoMock, elementDaoMock);
            cardRepository.getCardCache().clear();
            UUID userId = UUID.randomUUID();
            cardRepository.getCardCache().put(UUID.randomUUID(), new CardDTO(UUID.randomUUID(), 0, "spell", "water", userId, null, false, false));
            cardRepository.getCardCache().put(UUID.randomUUID(), new CardDTO(UUID.randomUUID(), 0, "spell", "water", userId, null, false, false));

            // act
            ArrayList<CardDTO> cards = cardRepository.getForUser(UUID.randomUUID());

            // assert
            assertEquals(0, cards.size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test: get cards for user expect list size 2")
    public void getCardsForUser_ExpectListSize2() {
        try {
            // arrange
            CardDao cardDaoMock = mock(CardDao.class);
            CardtypeDao cardtypeDaoMock = mock(CardtypeDao.class);
            ElementDao elementDaoMock = mock(ElementDao.class);

            HashMap<Integer, Cardtype> cardtypes = new HashMap<>();
            cardtypes.put(1, new Cardtype(1, "spell", false));
            cardtypes.put(2, new Cardtype(2, "goblin", true));
            when(cardtypeDaoMock.read()).thenReturn(cardtypes);

            HashMap<Integer, Element> elements = new HashMap<>();
            elements.put(1, new Element(1, "fire"));
            elements.put(2, new Element(2, "water"));
            elements.put(3, new Element(3, "normal"));
            when(elementDaoMock.read()).thenReturn(elements);

            CardRepository cardRepository = new CardRepository(cardDaoMock, cardtypeDaoMock, elementDaoMock);
            cardRepository.getCardCache().clear();
            UUID userId = UUID.randomUUID();
            cardRepository.getCardCache().put(UUID.randomUUID(), new CardDTO(UUID.randomUUID(), 0, "spell", "water", userId, null, false, false));
            cardRepository.getCardCache().put(UUID.randomUUID(), new CardDTO(UUID.randomUUID(), 0, "spell", "water", userId, null, false, false));

            // act
            ArrayList<CardDTO> cards = cardRepository.getForUser(userId);

            // assert
            assertEquals(2, cards.size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
