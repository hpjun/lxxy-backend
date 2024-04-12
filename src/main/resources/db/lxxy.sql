drop database lxxy;

-- 创建库
create database if not exists lxxy;

-- 切换库
use lxxy;

-- 用户表
create table if not exists user
(
    id          bigint auto_increment comment 'id' primary key,
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint      default 0                 not null comment '是否删除',
    phone       varchar(11) unique                     not null comment '手机号',
    email       varchar(256)                           null comment '邮箱',
    birthday    varchar(50)                            null comment '生日',
    username    varchar(256)                           not null comment '用户名',
    password    varchar(256)                           not null comment '密码',
    avatar      varchar(1024)                          not null comment '用户头像',
    profile     varchar(1024)                          not null comment '用户简介',
    role        varchar(256) default 'user'            not null comment '用户角色：user/admin',
    sex         varchar(10)                            null comment '性别：男、女、保密',
    address     varchar(512)                           null comment '地址',
    school      varchar(256)                           null comment '学校',
    u_class     varchar(256)                           null comment '班级',
    ban         tinyint      default 0                 not null comment '是否封禁',

    index idx_phone (phone)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id            bigint auto_increment comment 'id' primary key,
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint  default 0                 not null comment '是否删除',
    ip_region     varchar(50)                        not null comment 'ip属地',
    title         varchar(512)                       not null comment '标题',
    content       text                               not null comment '内容',
    pic_url_list  varchar(2048)                      not null comment '帖子图片(json 数组)',
    comment_count bigint   default 0                 not null comment '评论数',
    view_count    bigint   default 0                 not null comment '浏览量',
    post_type_id  int                                not null comment '帖子板块id',
    user_id       bigint                             not null comment '创建用户 id',
    is_top        tinyint  default 0                 not null comment '是否置顶',
    lc_time       datetime default CURRENT_TIMESTAMP not null comment '最近评论时间',
    username      varchar(256)                       not null comment '创建用户名称',
    user_avatar   varchar(1024)                      not null comment '创建用户头像',
    is_show       tinyint  default 1                 not null comment '是否可见',


    index idx_post_type_id (post_type_id)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子板块表
create table if not exists post_type
(
    id          bigint auto_increment comment 'id' primary key,
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    type_name   varchar(50) unique                 not null comment '板块名称',
    status      tinyint  default 1                 not null comment '板块状态：0封禁1启用'

) comment '帖子板块' collate = utf8mb4_unicode_ci;

-- 活动表
create table if not exists activity
(
    id          bigint auto_increment comment 'id' primary key,
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    title       varchar(512)                       not null comment '标题',
    profile     text                               not null comment '活动简介',
    level       tinyint                            not null comment '活动难度：0简单1休闲2困难',
    pic_url     varchar(1024)                      not null comment '活动封面(json 数组)',
    venue       varchar(512)                       not null comment '集合点',
    distance    int                                not null comment '活动路程KM',
    total_count int                                not null comment '活动总人数',
    user_id     bigint                             not null comment '创建用户 id',
    start_time  datetime                           not null comment '开始时间',
    end_time    datetime                           not null comment '结束时间',
    contact     varchar(11)                        not null comment '活动发起人手机号',
    status      varchar(20)                        not null comment '活动状态：未开始、进行中、结束',
    is_top      tinyint  default 0                 not null comment '是否置顶',
    username    varchar(256)                       not null comment '创建用户名称',
    user_avatar varchar(1024)                      not null comment '创建用户头像',
    is_show     tinyint  default 1                 not null comment '是否可见',

    index idx_level (level),
    index idx_start_end_time (start_time, end_time)
) comment '活动' collate = utf8mb4_unicode_ci;

-- 活动成员表-硬删除
create table if not exists activity_member
(
    id          bigint auto_increment comment 'id' primary key,
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    activity_id bigint                             not null comment '活动id',
    user_id     bigint                             not null comment '参与用户 id',
    username    varchar(256)                       not null comment '参与用户名称',
    user_avatar varchar(1024)                      not null comment '参与用户头像',

    unique key uniq_activity_user (activity_id, user_id),

    index idx_activity_id (activity_id)
) comment '活动成员' collate = utf8mb4_unicode_ci;

-- 帖子评论表-硬删除
create table if not exists post_comment
(
    id          bigint auto_increment comment 'id' primary key,
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    user_id     bigint                             not null comment '评论用户 id',
    post_id     bigint                             not null comment '被评论帖子id',
    ip_region   varchar(50)                        not null comment 'ip属地',
    username    varchar(256)                       not null comment '创建用户名称',
    user_avatar varchar(1024)                      not null comment '创建用户头像',
    comment     varchar(1024)                      not null comment '评论内容最大字数1000',

    index idx_post_id (post_id)
) comment '帖子评论' collate = utf8mb4_unicode_ci;

-- 用户关注表-硬删除
create table if not exists follow
(
    id             bigint auto_increment comment 'id' primary key,
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    user_id        bigint                             not null comment '用户id',
    follow_user_id bigint                             not null comment '被关注用户id',

    unique key uniq_user_follow (user_id, follow_user_id),

    index idx_user_id (user_id),
    index inx_follow_user_id (follow_user_id)
) comment '关注' collate = utf8mb4_unicode_ci;

-- 收藏表-硬删除
create table if not exists favorites
(
    id          bigint auto_increment comment 'id' primary key,
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    user_id     bigint                             not null comment '用户id',
    post_id     bigint                             not null comment '收藏的帖子id',

    unique key uniq_user_post (user_id, post_id),

    index idx_user_id (user_id)
) comment '收藏' collate = utf8mb4_unicode_ci;

insert into user(phone, username, password, avatar, profile, sex)
values ('13888888888',
        '13888888888',
        '4297f44b13955235245b2497399d7a93',
        '/img/avatar.png',
        '该用户很神秘，什么都没有写',
        '保密');