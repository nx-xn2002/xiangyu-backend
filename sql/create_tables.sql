create table if not exists tag
(
    id           bigint auto_increment
        primary key,
    tagName      varchar(256)                       null comment '标签名称',
    userId       bigint                             null comment '用户id',
    parentId     bigint                             null comment '父标签id',
    isParent     tinyint                            null comment '0 - 不是, 1 - 是父标签',
    create_time  datetime default (now())           null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    deleted_flag tinyint  default 0                 not null comment '删除状态 默认0',
    constraint unique_tagName
        unique (tagName)
)
    comment '标签';

create index idx_userId
    on tag (userId);


create table if not exists user
(
    id            bigint auto_increment
        primary key,
    user_account  varchar(256)                       null comment '登录账号',
    username      varchar(256)                       null comment '用户昵称',
    avatar_url    varchar(1024)                      null comment '用户头像',
    gender        tinyint                            null comment '性别',
    profile       varchar(512)                       null comment '个人简介',
    user_password varchar(512)                       not null comment '密码',
    phone         varchar(128)                       null comment '电话',
    email         varchar(512)                       null comment '邮箱',
    user_status   int      default 0                 not null comment '用户状态 0 正常',
    user_role     int      default 0                 null comment '用户角色 0-普通用户 1-管理员',
    create_time   datetime default (now())           null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    deleted_flag  tinyint  default 0                 not null comment '删除状态 默认0',
    tags          varchar(1024)                      null comment '标签列表'
)
    comment '用户';

create table if not exists team
(
    id           bigint auto_increment
        primary key,
    name         varchar(256)                       not null comment '队伍名称',
    description  varchar(1024)                      null comment '描述',
    maxNum       int      default 1                 not null comment '最大人数',
    expireTime   datetime                           null comment '过期时间',
    userId       bigint comment '用户id',
    status       int      default 0                 not null comment '队伍状态 0-公开 1-私有 2-加密',
    password     varchar(512)                       null comment '密码',
    create_time  datetime default (now())           null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    deleted_flag tinyint  default 0                 not null comment '删除状态 默认0'
)
    comment '队伍';

create table if not exists user_team
(
    id          bigint auto_increment
        primary key,
    userId      bigint comment '用户id',
    teamId      bigint comment '队伍id',
    joinTime    datetime                           null comment '加入时间',
    createTime  datetime default (now())           null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    deletedFlag tinyint  default 0                 not null comment '删除状态 默认0'
)
    comment '用户 - 队伍表';

