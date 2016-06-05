# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table achievements (
  ach_id                    integer not null,
  ach_owner_user_id         integer,
  ach_title                 varchar(255),
  ach_date                  timestamp,
  ach_date_add              timestamp,
  ach_cat_sub_cat_id        integer,
  ach_comment               VARCHAR(4095),
  ach_dop                   VARCHAR(4095),
  ach_prem_status           integer,
  ach_stip_status           integer,
  constraint pk_achievements primary key (ach_id))
;

create table categories (
  cat_id                    integer not null,
  cat_title                 varchar(255),
  constraint pk_categories primary key (cat_id))
;

create table faculties (
  fcl_id                    integer not null,
  fcl_long_title            varchar(255),
  fcl_title                 varchar(255),
  fcl_adress                VARCHAR(2047),
  constraint uq_faculties_fcl_long_title unique (fcl_long_title),
  constraint pk_faculties primary key (fcl_id))
;

create table md (
  md_id                     integer not null,
  md_user_user_id           integer,
  md_faculty_fcl_id         integer,
  constraint pk_md primary key (md_id))
;

create table roles (
  role_id                   integer not null,
  role_title                varchar(255),
  constraint pk_roles primary key (role_id))
;

create table sub_categories (
  sub_cat_id                integer not null,
  sub_cat_definition        VARCHAR(4095),
  sub_cat_alias             varchar(255),
  mark                      double,
  parent_cat_cat_id         integer,
  constraint pk_sub_categories primary key (sub_cat_id))
;

create table users (
  user_id                   integer not null,
  login                     varchar(255) not null,
  user_first_name           varchar(255),
  user_last_name            varchar(255),
  pass                      varchar(255),
  user_faculty_fcl_id       integer,
  user_reg                  timestamp,
  user_stip_cat_id          integer,
  user_status               boolean,
  user_role_role_id         integer,
  constraint uq_users_login unique (login),
  constraint pk_users primary key (user_id))
;

create sequence achievements_seq;

create sequence categories_seq;

create sequence faculties_seq;

create sequence md_seq;

create sequence roles_seq;

create sequence sub_categories_seq;

create sequence users_seq;

alter table achievements add constraint fk_achievements_achOwner_1 foreign key (ach_owner_user_id) references users (user_id) on delete restrict on update restrict;
create index ix_achievements_achOwner_1 on achievements (ach_owner_user_id);
alter table achievements add constraint fk_achievements_achCat_2 foreign key (ach_cat_sub_cat_id) references sub_categories (sub_cat_id) on delete restrict on update restrict;
create index ix_achievements_achCat_2 on achievements (ach_cat_sub_cat_id);
alter table md add constraint fk_md_mdUser_3 foreign key (md_user_user_id) references users (user_id) on delete restrict on update restrict;
create index ix_md_mdUser_3 on md (md_user_user_id);
alter table md add constraint fk_md_mdFaculty_4 foreign key (md_faculty_fcl_id) references faculties (fcl_id) on delete restrict on update restrict;
create index ix_md_mdFaculty_4 on md (md_faculty_fcl_id);
alter table sub_categories add constraint fk_sub_categories_parentCat_5 foreign key (parent_cat_cat_id) references categories (cat_id) on delete restrict on update restrict;
create index ix_sub_categories_parentCat_5 on sub_categories (parent_cat_cat_id);
alter table users add constraint fk_users_userFaculty_6 foreign key (user_faculty_fcl_id) references faculties (fcl_id) on delete restrict on update restrict;
create index ix_users_userFaculty_6 on users (user_faculty_fcl_id);
alter table users add constraint fk_users_userStip_7 foreign key (user_stip_cat_id) references categories (cat_id) on delete restrict on update restrict;
create index ix_users_userStip_7 on users (user_stip_cat_id);
alter table users add constraint fk_users_userRole_8 foreign key (user_role_role_id) references roles (role_id) on delete restrict on update restrict;
create index ix_users_userRole_8 on users (user_role_role_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists achievements;

drop table if exists categories;

drop table if exists faculties;

drop table if exists md;

drop table if exists roles;

drop table if exists sub_categories;

drop table if exists users;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists achievements_seq;

drop sequence if exists categories_seq;

drop sequence if exists faculties_seq;

drop sequence if exists md_seq;

drop sequence if exists roles_seq;

drop sequence if exists sub_categories_seq;

drop sequence if exists users_seq;

