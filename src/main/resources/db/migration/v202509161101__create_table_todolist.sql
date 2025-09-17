
create table todos
(
    id        integer,
    text      varchar(255),
    completed boolean
);

alter table todos
    owner to postgres;

