create database auto;
use auto;
create table data
(
    stu_id varchar(16)  not null
        primary key,
    url    varchar(255) null,
    push_uid varchar(100) default null
);