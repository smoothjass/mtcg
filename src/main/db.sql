--DROP TABLES-----------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS tradings;
DROP TABLE IF EXISTS stack;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS cardtypes;
DROP TABLE IF EXISTS elements;

--CREATE TABLES---------------------------------------------------------------------------------------------------
CREATE TABLE users (
                       user_id UUID PRIMARY KEY,
                       password VARCHAR (50) NOT NULL,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       elo SMALLINT NOT NULL,
                       games_played SMALLINT NOT NULL,
                       games_won SMALLINT NOT NULL,
                       coins SMALLINT NOT NULL
);

CREATE TABLE cardtypes (
                           cardtype_id SERIAL PRIMARY KEY,
                           name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE elements (
                          element_id SERIAL PRIMARY KEY,
                          name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE cards (
                       card_id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       damage SMALLINT NOT NULL,
                       cardtype_id INT NOT NULL,
                       CONSTRAINT fk_cardtype
                           FOREIGN KEY(cardtype_id)
                               REFERENCES cardtypes(cardtype_id),
                       element_id INT NOT NULL,
                       CONSTRAINT fk_element
                           FOREIGN KEY(element_id)
                               REFERENCES elements(element_id)
);

CREATE TABLE stack (
                       cardinstance_id SERIAL PRIMARY KEY,
                       card_id INT NOT NULL,
                       CONSTRAINT fk_card
                           FOREIGN KEY(card_id)
                               REFERENCES cards(card_id),
                       user_id UUID NOT NULL,
                       CONSTRAINT fk_user
                           FOREIGN KEY(user_id)
                               REFERENCES users(user_id),
                       used_in_deck BOOLEAN NOT NULL,
                       used_in_trade BOOLEAN NOT NULL
);

CREATE TABLE tradings (
                          trading_id UUID PRIMARY KEY,
                          cardinstance_id INT NOT NULL,
                          CONSTRAINT fk_cardinstance
                              FOREIGN KEY(cardinstance_id)
                                  REFERENCES stack(cardinstance_id),
                          card_id INT,
                          CONSTRAINT fk_card
                              FOREIGN KEY(card_id)
                                  REFERENCES cards(card_id),
                          cardtype_id INT,
                          CONSTRAINT fk_cardtype
                              FOREIGN KEY(cardtype_id)
                                  REFERENCES cardtypes(cardtype_id),
                          element_id INT,
                          CONSTRAINT fk_element
                              FOREIGN KEY(element_id)
                                  REFERENCES elements(element_id),
                          min_damage SMALLINT
);

--SET DEFAULT VALUES----------------------------------------------------------------------------------------------
ALTER TABLE users ALTER COLUMN elo SET DEFAULT 100;
ALTER TABLE users ALTER COLUMN games_played SET DEFAULT 0;
ALTER TABLE users ALTER COLUMN games_won SET DEFAULT 0;
ALTER TABLE users ALTER COLUMN coins SET DEFAULT 20;
ALTER TABLE stack ALTER COLUMN used_in_deck SET DEFAULT false;
ALTER TABLE stack ALTER COLUMN used_in_trade SET DEFAULT false;

--INSERTS---------------------------------------------------------------------------------------------------------
INSERT INTO cardtypes (name) VALUES ('spell');
INSERT INTO cardtypes (name) VALUES ('goblin');
INSERT INTO cardtypes (name) VALUES ('dragon');
INSERT INTO cardtypes (name) VALUES ('wizzard');
INSERT INTO cardtypes (name) VALUES ('ork');
INSERT INTO cardtypes (name) VALUES ('knight');
INSERT INTO cardtypes (name) VALUES ('kraken');
INSERT INTO cardtypes (name) VALUES ('elf');

INSERT INTO elements (name) VALUES ('fire');
INSERT INTO elements (name) VALUES ('water');
INSERT INTO elements (name) VALUES ('normal');

--spells
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('weak', 15, 1, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('weak', 15, 1, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('weak', 15, 1, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 25, 1, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 25, 1, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 25, 1, 3);

--goblins
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 2, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 2, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 2, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 2, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 2, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 2, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 2, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 2, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 2, 3);

--dragons
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 3, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 3, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 3, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 3, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 3, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 3, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 3, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 3, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 3, 3);

--wizzards
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 4, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 4, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 4, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 4, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 4, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 4, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 4, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 4, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 4, 3);

--orks
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 5, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 5, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 5, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 5, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 5, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 5, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 5, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 5, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 5, 3);

--knights
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 6, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 6, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 6, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 6, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 6, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 6, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 6, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 6, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 6, 3);

--kraken
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 7, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 7, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 7, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 7, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 7, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 7, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 7, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 7, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 7, 3);

--elves
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 8, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 8, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('baby', 10, 8, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 8, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 8, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('adolescent', 20, 8, 3);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 8, 1);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 8, 2);
INSERT INTO cards (name, damage, cardtype_id, element_id) VALUES ('powerful', 30, 8, 3);

select
    cards.name || e.name || c.name as name, cards.damage, c.cardtype_id, e.element_id
from cards
    join cardtypes c on c.cardtype_id = cards.cardtype_id
    join elements e on e.element_id = cards.element_id;