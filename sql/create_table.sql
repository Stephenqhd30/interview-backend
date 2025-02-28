# 数据库初始化
# @author stephen qiu
#

-- 创建库
create database if not exists interview;

-- 切换库
use interview;

-- 用户表
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userPhone    varchar(256)                           null comment '手机号码',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    userEmail    varchar(256)                           null comment '用户邮箱',
    userGender   tinyint      default 2                 null comment '用户性别(0-男, 1-女, 2-保密)',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
)
    comment '用户' collate = utf8mb4_unicode_ci;

-- 题库表
create table question_bank
(
    id          bigint auto_increment comment 'id'
        primary key,
    title       varchar(256)                       null comment '标题',
    description text                               null comment '描述',
    picture     varchar(2048)                      null comment '图片',
    userId      bigint                             not null comment '创建用户id',
    editTime    datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '题库表';

create index title
    on question_bank (title)
    comment '题目标题索引';


-- 题目表
create table question
(
    id         bigint auto_increment comment 'id'
        primary key,
    title      varchar(256)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json数组）',
    answer     text                               null comment '推荐答案',
    userId     bigint                             not null comment '创建用户 id',
    editTime   datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '题目表';

create index idx_title
    on question (title);

create index idx_userId
    on question (userId);

-- 题库题目表（硬删除）
create table question_bank_question
(
    id             bigint auto_increment comment 'id'
        primary key,
    questionBankId bigint                             not null comment '题库id',
    questionId     bigint                             not null comment '题目id',
    userId         bigint                             not null comment '创建人id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint question_bank_question_pk
        unique (questionBankId, questionId)
)
    comment '题库题目表（硬删除）';

-- 题目点赞表（硬删除）
create table if not exists question_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    questionId     bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_questionIdId (questionId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 题目收藏表（硬删除）
create table if not exists question_favour
(
    id         bigint auto_increment comment 'id' primary key,
    questionId     bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '题目收藏';

-- 标签表
create table tag
(
    id         bigint auto_increment comment 'id'
        primary key,
    tagName    varchar(256)                       not null comment '标签名称',
    userId     bigint                             not null comment '用户id',
    parentId   bigint                             null comment '父标签id',
    isParent   tinyint  default 0                 null comment '0-不是父标签，1-是父标签',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '标签表';