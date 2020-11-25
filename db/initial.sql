create database `nine_nine`;
use `nine_nine`;

create table `user`
(
    id       int         not null primary key auto_increment,
    name     varchar(20) not null UNIQUE,
    password varchar(40) not null
);
