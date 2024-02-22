# 创建用户表
-- auto-generated definition
create table user
(
    id           int auto_increment
        primary key,
    username     varchar(126)                       null,
    userAccount  varchar(256)                       not null comment '账户',
    userPassword varchar(256)                       not null comment '密码',
    gender       tinyint                            null comment '性别
0 - 女
1 - 男',
    phone        varchar(256)                       null,
    email        varchar(256)                       null,
    plantCode    int                                null,
    createTime   datetime default CURRENT_TIMESTAMP null,
    updateTime   datetime default CURRENT_TIMESTAMP null,
    userRole     tinyint  default 0                 null comment '身份
0 - 普通用户
1 - 管理员',
    userStatus   tinyint  default 1                 null comment '是否有效
0 - 无效
1 - 有效',
    isDelete     tinyint  default 1                 null comment '是否逻辑删除
1 - 否
0 - 是',
    avatarUrl    varchar(1024)                      null,
    userAddress  varchar(512)                       null,
    tags         varchar(1024)                      null comment '用户标签',
    constraint userAccount
        unique (userAccount)
)
    comment '用户表';





# 新增用户表里面的字段tage  标签
alter table user add column tags varchar(1024) null comment '用户的所有标签';



# 创建标签表
create table tag
(
    id         int auto_increment
        primary key,
    tagName    varchar(256)                       null comment '标签名',
    userId     int                                null comment '用户id',
    parentId   int                                null comment '父标签id',
    isParent   tinyint                            null comment '是否为父标签
0 - 不是
1 - 是',
    createTime datetime default CURRENT_TIMESTAMP null,
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 1                 null comment '是否逻辑删除
1 - 否
0 - 是',
    constraint uniIdx__tagName
        unique (tagName)
)
    comment '用户表';

create index idx_userId
    on tag (userId);



# 队伍表
create table team
(
    id          int auto_increment   primary key,
    name  varchar(256)                       not null comment '队伍名称',
    description varchar(1024)                        null comment '描述',
    userId       int                             comment '创建队伍的用户id',
    maxNum        int  default 1         null comment '队伍最大人数',
    status        tinyint             null  comment '0-公开  1-私有  2-加密',
    password    varchar(512)                            null,
    createTime   datetime default CURRENT_TIMESTAMP null,
    updateTime   datetime default CURRENT_TIMESTAMP null,
    expireTime datetime null comment '过期时间',
        isDelete     tinyint  default 1                 null comment '是否逻辑删除
1 - 否
0 - 是'

)
    comment '队伍表';



# 用户队伍关系表
create table team
(
    id          int auto_increment   primary key,
    userId       int                             comment '加入队伍的用户的id',
    teamId        int            comment '队伍id',
    createTime   datetime default CURRENT_TIMESTAMP null,
    updateTime   datetime default CURRENT_TIMESTAMP null,
    joinTime datetime null comment '过期时间',
        isDelete     tinyint  default 1                 null comment '是否逻辑删除
1 - 否
0 - 是'

)
    comment '用户与队伍的关系表';
