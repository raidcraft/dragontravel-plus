-- apply changes
create table rc_flight_paths (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  start_station_id              bigint,
  end_station_id                bigint,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint uq_rc_flight_paths_name unique (name),
  constraint pk_rc_flight_paths primary key (id)
);

create table rc_flight_player_stations (
  id                            bigint auto_increment not null,
  station_id                    bigint,
  player                        varchar(255),
  player_id                     varchar(40),
  discovered                    datetime(6),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_rc_flight_player_stations primary key (id)
);

create table rc_flight_stations (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  display_name                  varchar(255),
  world                         varchar(255),
  x                             integer not null,
  y                             integer not null,
  z                             integer not null,
  cost_multiplier               double not null,
  main_station                  tinyint(1) default 0 not null,
  emergency_station             tinyint(1) default 0 not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint uq_rc_flight_stations_name unique (name),
  constraint pk_rc_flight_stations primary key (id)
);

create table rc_flight_waypoints (
  id                            bigint auto_increment not null,
  path_id                       bigint,
  waypoint_index                integer not null,
  world                         varchar(255),
  x                             double not null,
  y                             double not null,
  z                             double not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_rc_flight_waypoints primary key (id)
);

create index ix_rc_flight_paths_start_station_id on rc_flight_paths (start_station_id);
alter table rc_flight_paths add constraint fk_rc_flight_paths_start_station_id foreign key (start_station_id) references rc_flight_stations (id) on delete restrict on update restrict;

create index ix_rc_flight_paths_end_station_id on rc_flight_paths (end_station_id);
alter table rc_flight_paths add constraint fk_rc_flight_paths_end_station_id foreign key (end_station_id) references rc_flight_stations (id) on delete restrict on update restrict;

create index ix_rc_flight_player_stations_station_id on rc_flight_player_stations (station_id);
alter table rc_flight_player_stations add constraint fk_rc_flight_player_stations_station_id foreign key (station_id) references rc_flight_stations (id) on delete restrict on update restrict;

create index ix_rc_flight_waypoints_path_id on rc_flight_waypoints (path_id);
alter table rc_flight_waypoints add constraint fk_rc_flight_waypoints_path_id foreign key (path_id) references rc_flight_paths (id) on delete restrict on update restrict;

