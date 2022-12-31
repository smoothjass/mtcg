--DROP TABLES-----------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS tradings CASCADE;
DROP TABLE IF EXISTS stack CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS cards CASCADE;
DROP TABLE IF EXISTS cardtypes CASCADE;
DROP TABLE IF EXISTS elements CASCADE;

create table if not exists roles
(
    role_id serial
    primary key,
    name    varchar(255)
    );

alter table roles
    owner to swe1user;

create table if not exists users
(
    user_id      uuid                 not null
    primary key,
    password     varchar(255)         not null,
    username     varchar(255)         not null
    unique,
    role_id      integer  default 2   not null
    constraint fk_role
    references roles,
    elo          smallint default 100 not null,
    games_played smallint default 0   not null,
    games_won    smallint default 0   not null,
    coins        smallint default 20  not null
    );

alter table users
    owner to swe1user;

create table if not exists cardtypes
(
    cardtype_id serial
    primary key,
    name        varchar(255)         not null
    unique,
    monster     boolean default true not null
    );

alter table cardtypes
    owner to swe1user;

create table if not exists elements
(
    element_id serial
    primary key,
    name       varchar(255) not null
    unique
    );

alter table elements
    owner to swe1user;

create table if not exists cards
(
    card_id     serial
    primary key,
    damage      smallint not null,
    cardtype_id integer  not null
    constraint fk_cardtype
    references cardtypes,
    element_id  integer  not null
    constraint fk_element
    references elements
);

alter table cards
    owner to swe1user;

create table if not exists packages
(
    package_id serial
    primary key
);

alter table packages
    owner to swe1user;

create table if not exists cards_packages
(
    package_id integer not null
    constraint fk_package
    references packages,
    card_id    integer not null
    constraint fk_card
    references cards,
    primary key (package_id, card_id)
    );

alter table cards_packages
    owner to swe1user;

create table if not exists stacks
(
    cardinstance_id serial
    primary key,
    card_id         integer               not null
    constraint fk_card
    references cards,
    user_id         uuid                  not null
    constraint fk_user
    references users,
    used_in_deck    boolean default false not null,
    used_in_trade   boolean default false not null
);

alter table stacks
    owner to swe1user;

create table if not exists tradings
(
    trading_id      uuid    not null
    primary key,
    cardinstance_id integer not null
    constraint fk_cardinstance
    references stacks,
    card_id         integer
    constraint fk_card
    references cards,
    cardtype_id     integer
    constraint fk_cardtype
    references cardtypes,
    element_id      integer
    constraint fk_element
    references elements,
    min_damage      smallint
);

alter table tradings
    owner to swe1user;


--INSERTS---------------------------------------------------------------------------------------------------------
INSERT INTO roles (name) VALUES ('admin');
INSERT INTO roles (name) VALUES ('user');

--pw hashed SHA256
INSERT INTO users (user_id, password, username, role_id) VALUES
    (gen_random_uuid(), '5fcf82bc15aef42cd3ec93e6d4b51c04df110cf77ee715f62f3f172ff8ed9de9', 'admin', 1);
--pw: adminpw
INSERT INTO users (user_id, password, username, role_id) VALUES
    (gen_random_uuid(), '80280cec33e73ad0b9cc9c184ccdd7b49bbca06a7c2bf969d44f97b60f101177', 'testuser', 2);
--pw: testuserpw
INSERT INTO users (user_id, password, username, role_id) VALUES
    (gen_random_uuid(), 'b285a60f7e0e3d95e5f0eaf3e86df409e1082b6f6d545c993f1de9b1100504fb', 'seconduser', 2);
--pw: seconduserpw

INSERT INTO cardtypes (name, monster) VALUES ('spell', false);
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