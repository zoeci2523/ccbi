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

-- task table
create table if not exists task
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                           not null comment 'user id',
    contentId    bigint                           not null comment 'content id',
    type         tinyint(4)                       UNSIGNED not null comment 'task type',
    status       tinyint(4)                       UNSIGNED null comment 'task progress: 0-init, 1-wait, 2-running, 3-succeed, 4-failed',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'created time',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'updated time',
    isDelete     tinyint      default 0                 not null comment 'delete status'
) comment 'task' collate = utf8mb4_unicode_ci;

-- chart
create table if not exists chart_detail
(
    id           bigint auto_increment comment 'id' primary key,
    goal		 text  null comment 'analysis goal',
    `name`               varchar(128) null comment 'chart name',
    chartData    text  null comment 'chart raw data',
    chartType	   varchar(128) null comment 'chart type',
    genChart		 text	 null comment 'generated chart data',
    genResult		 text	 null comment 'generated result',
    execMessage  text   null comment 'execute message',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'created time',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'updated time',
    isDelete     tinyint      default 0                 not null comment 'delete status'
) comment 'chart_detail' collate = utf8mb4_unicode_ci;

