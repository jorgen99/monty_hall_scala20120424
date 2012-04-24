# --- !Ups

set ignorecase true;

alter table game drop column selectedDoor;
alter table game add column initialPlayerDoor int;

alter table game add column carDoor   int;
alter table game add column switched  boolean;
alter table game add column won       boolean;
alter table game add column gameOver  boolean;

# --- !Downs

alter table game drop column initialPlayerDoor;
alter table game add column selectedDoor int;

alter table game drop column carDoor;
alter table game drop column switched;
alter table game drop column won;
alter table game drop column gameOver;

