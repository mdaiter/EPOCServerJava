# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table boolean_extend (
  id                        bigint not null,
  attribute                 varchar(255),
  bool                      boolean,
  log_item_date             timestamp,
  constraint pk_boolean_extend primary key (id))
;

create table byte_extend (
  id                        bigint not null,
  attribute                 varchar(255),
  byt                       tinyint,
  log_item_date             timestamp,
  constraint pk_byte_extend primary key (id))
;

create table client_app_log (
  client_uuid               varchar(255) not null,
  u_app_app_uuid            varchar(255),
  constraint pk_client_app_log primary key (client_uuid))
;

create table user (
  username                  varchar(255) not null,
  password                  varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  constraint pk_user primary key (username))
;

create table user_app (
  app_uuid                  varchar(255) not null,
  app_nam                   varchar(255),
  user_username             varchar(255),
  constraint pk_user_app primary key (app_uuid))
;

create table user_app_log_item (
  date                      timestamp not null,
  app_name                  varchar(255),
  time_has_run              integer,
  name_of_user_app_log      varchar(255),
  c_app_log_client_uuid     varchar(255),
  constraint pk_user_app_log_item primary key (date))
;

create sequence boolean_extend_seq;

create sequence byte_extend_seq;

create sequence client_app_log_seq;

create sequence user_seq;

create sequence user_app_seq;

create sequence user_app_log_item_seq;

alter table boolean_extend add constraint fk_boolean_extend_logItem_1 foreign key (log_item_date) references user_app_log_item (date) on delete restrict on update restrict;
create index ix_boolean_extend_logItem_1 on boolean_extend (log_item_date);
alter table byte_extend add constraint fk_byte_extend_logItem_2 foreign key (log_item_date) references user_app_log_item (date) on delete restrict on update restrict;
create index ix_byte_extend_logItem_2 on byte_extend (log_item_date);
alter table client_app_log add constraint fk_client_app_log_uApp_3 foreign key (u_app_app_uuid) references user_app (app_uuid) on delete restrict on update restrict;
create index ix_client_app_log_uApp_3 on client_app_log (u_app_app_uuid);
alter table user_app add constraint fk_user_app_user_4 foreign key (user_username) references user (username) on delete restrict on update restrict;
create index ix_user_app_user_4 on user_app (user_username);
alter table user_app_log_item add constraint fk_user_app_log_item_cAppLog_5 foreign key (c_app_log_client_uuid) references client_app_log (client_uuid) on delete restrict on update restrict;
create index ix_user_app_log_item_cAppLog_5 on user_app_log_item (c_app_log_client_uuid);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists boolean_extend;

drop table if exists byte_extend;

drop table if exists client_app_log;

drop table if exists user;

drop table if exists user_app;

drop table if exists user_app_log_item;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists boolean_extend_seq;

drop sequence if exists byte_extend_seq;

drop sequence if exists client_app_log_seq;

drop sequence if exists user_seq;

drop sequence if exists user_app_seq;

drop sequence if exists user_app_log_item_seq;

