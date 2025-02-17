
create schema if not exists braindeck;

create table braindeck.t_languages(
                                      id serial primary key,
                                      name varchar(25) not null check (length(trim(name))>=2 ),
                                      top boolean NOT NULL DEFAULT FALSE
);

create table braindeck.t_users (
                                   id serial primary key,
                                   name varchar(50) not null unique,
                                   email varchar(50) not null unique,
                                   password varchar(70) not null,
                                   created_at timestamp default current_timestamp
);

create table braindeck.t_folders (
                                     id serial primary key,
                                     name varchar(50) not null,
                                     user_id integer not null references braindeck.t_users(id) on delete cascade,
                                     created_at timestamp default current_timestamp
);



create table braindeck.t_sets (
                                  id serial primary key,
                                  title varchar(50) not null check (length(trim(title)) >= 1),
                                  description varchar(500) not null check (length(trim(description)) >= 1),
                                  termLanguageId  integer not null references braindeck.t_languages(id),
                                  descriptionLanguageId  integer not null references braindeck.t_languages(id),
                                  user_id integer not null references braindeck.t_users(id) on delete cascade,
                                  created_at timestamp default current_timestamp
);
create table braindeck.t_folder_set (
                                        folder_id integer not null references braindeck.t_folders(id) on delete cascade,
                                        set_id integer not null references braindeck.t_sets(id) on delete cascade
);
create table braindeck.t_terms (
                                   id serial primary key ,
                                   setId integer not null references braindeck.t_sets(id) on delete cascade,
                                   term varchar(950) not null check (length(trim(term)) >= 1),
                                   description varchar(950)

);

