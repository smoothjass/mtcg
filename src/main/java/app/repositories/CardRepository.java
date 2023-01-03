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
            HashMap<Integer, Cardtype> cardtypes = getCardtypeDao().read();
            HashMap<Integer, Element> elements = getElementDao().read();
            HashMap<UUID, Card> cards = getCardDao().read();
            for(Card card: new ArrayList<>(cards.values())) {
                CardDTO cardDTO = new CardDTO(
                    card.getId(),
                    card.getDamage(),
                    cardtypes.get(card.getCardtype_id()).getName(),
                    elements.get(card.getElement_id()).getName(),
                    card.getUser_id(),
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
            for (CardDTO card : newCards) {
                if (cards.get(card.getId()) != null) {
                    return null;
                }
            }
        }
        // TODO add cards to db, return packageDTO, add cards to cardCache
        // CONTINUE HERE
        return null;
    }
}
