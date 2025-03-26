drop table if exists Permission cascade;
drop table if exists FileLock cascade;
drop table if exists File cascade;
drop table if exists Permission cascade;
drop table if exists Account;

create table if not exists Account(
    id serial primary key,
    name varchar(100) not null,
    password varchar(255) not null
);

create table if not exists File(
    id serial primary key,
    name varchar(100) not null,
    created_by int references Account,
    creation_date timestamp,
    updated_by int references Account,
    update_date timestamp,
    last_locked_by int references Account,
    is_locked boolean,
    content varchar(1000) not null,
    version int
);

create table if not exists Permission(
    id serial primary key,
    file int references File,
    account int references Account,
    is_viewable boolean,
    is_editable boolean,
    is_deletable boolean
);

insert into Account(id, name, password) values (1, 'u1', 'p1');
insert into Account(id, name, password) values (2, 'u2', 'p2');

