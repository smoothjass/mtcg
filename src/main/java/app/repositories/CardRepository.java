package app.repositories;

import app.daos.CardDao;
import app.daos.CardtypeDao;
import app.daos.ElementDao;
import app.dtos.CardDTO;
import app.dtos.PackageDTO;
import app.models.Card;
import app.models.Cardtype;
import app.models.Element;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import static java.lang.Integer.parseInt;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class CardRepository {
    CardDao cardDao;
    CardtypeDao cardtypeDao;
    ElementDao elementDao;

    HashMap<UUID, CardDTO> cardCache = new HashMap<>();
    HashMap<Integer, Cardtype> cardtypeCache = new HashMap<>();
    HashMap<String, Integer> inverseCardtypeCache = new HashMap<>();
    HashMap<Integer, Element> elementCache = new HashMap<>();
    HashMap<String, Integer> inverseElementCache = new HashMap<>();
    @Getter
    HashMap<UUID, PackageDTO> packageCache = new HashMap<>();

    public CardRepository(CardDao cardDao, CardtypeDao cardtypeDao, ElementDao elementDao) {
        setCardDao(cardDao);
        setCardtypeDao(cardtypeDao);
        setElementDao(elementDao);
    }

    public HashMap<UUID, CardDTO> getAll() {
        try {
            if (!cardCache.isEmpty()) {
                return cardCache;
            }
            if (cardtypeCache.isEmpty()) {
                cardtypeCache = getCardtypeDao().read();
            }
            if(elementCache.isEmpty()) {
                elementCache = getElementDao().read();
            }
            HashMap<UUID, Card> cards = getCardDao().read();
            for(Card card: new ArrayList<>(cards.values())) {
                CardDTO cardDTO = new CardDTO(
                    card.getId(),
                    card.getDamage(),
                    cardtypeCache.get(card.getCardtype_id()).getName(),
                    elementCache.get(card.getElement_id()).getName(),
                    card.getUser_id(),
                    card.getPackage_id(),
                    card.isUsed_in_deck(),
                    card.isUsed_in_trade()
                );
                cardCache.put(cardDTO.getId(), cardDTO);
            }
            return cardCache;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PackageDTO postPackage(ArrayList<CardDTO> newCards) {
        // get all cards to see if they already exist.
        // use hashmap so we have roughly O(1) instead of O(4*number of cards)
        HashMap<UUID, CardDTO> cards = getAll();
        if (!cards.isEmpty()) {
            for (CardDTO card: newCards) {
                if (cards.get(card.getId()) != null) {
                    return null;
                }
            }
        }
        try {
        if (inverseCardtypeCache.isEmpty()) {
            if (cardtypeCache.isEmpty()) {
                cardtypeCache = getCardtypeDao().read();
            }
            cardtypeCache.forEach((key, value) -> {
                inverseCardtypeCache.put(value.getName(), key);
            });
        }
        if (inverseElementCache.isEmpty()) {
            if (elementCache.isEmpty()) {
                elementCache = getElementDao().read();
            }
            elementCache.forEach((key, value) -> {
                inverseElementCache.put(value.getName(), key);
            });
        }
        UUID packageId = UUID.randomUUID();
        ArrayList<CardDTO> tempCards = new ArrayList<>();
        for (CardDTO newCard: newCards) {
            Card card = new Card(
                newCard.getId(),
                newCard.getDamage(),
                inverseCardtypeCache.get(newCard.getCardtype()),
                inverseElementCache.get(newCard.getElement()),
                null,
                packageId,
                false,
                false
            );
            Card tempCard = getCardDao().create(card);
            CardDTO tempDTO = new CardDTO(
                tempCard.getId(),
                tempCard.getDamage(),
                cardtypeCache.get(tempCard.getCardtype_id()).getName(),
                elementCache.get(tempCard.getElement_id()).getName(),
                tempCard.getUser_id(),
                card.getPackage_id(),
                tempCard.isUsed_in_deck(),
                tempCard.isUsed_in_trade()
            );
            cardCache.put(tempDTO.getId(), tempDTO);
            tempCards.add(tempDTO);
        }
        PackageDTO newPackage = new PackageDTO(packageId, tempCards);
        return newPackage;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
