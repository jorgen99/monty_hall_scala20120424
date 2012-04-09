# --- First database schema

# --- !Ups

set ignorecase true;

create table game (
  id                        bigint not null,
  playerName                varchar(255) not null,
  selectedDoor              int,
  constraint pk_game primary key (id))
;

create sequence game_seq start with 1000;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists game;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists game_seq;
