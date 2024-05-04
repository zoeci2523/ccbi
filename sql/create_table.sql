# database / db table initialization

create database if not exists ccbi;

use ccbi;

-- user table
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    account      varchar(256)                           not null comment 'user account',
    password     varchar(512)                           not null comment 'user account password',
    unionId      varchar(256)                           null comment 'wechat open platform id',
    mpOpenId     varchar(256)                           null comment 'wechat mp(media platform) openId',
    username     varchar(256)                           null comment 'username, can be duplicated',
    avatar       varchar(1024)                          null comment 'user avatar, url',
    profile      varchar(512)                           null comment 'user profile',
    role         varchar(256) default 'user'            not null comment 'user role：user/admin/ban, default user',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'created time',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'updated time',
    isDelete     tinyint      default 0                 not null comment 'delete status',
    index idx_unionId (unionId)
) comment 'user' collate = utf8mb4_unicode_ci;

