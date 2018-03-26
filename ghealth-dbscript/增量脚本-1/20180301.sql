# 代理商资源表
DROP TABLE IF EXISTS `GHEALTH_AGENCY_RESOURCE`;
CREATE TABLE `GHEALTH_AGENCY_RESOURCE`
(
    `ID`                varchar(64)         NOT NULL        COMMENT '主键',
    `NAME`              varchar(128)        NOT NULL,
    `URI`               varchar(256)        NOT NULL,
    CONSTRAINT PK_AGENCY_RESOURCE PRIMARY KEY (`ID`)
)
;
ALTER TABLE `GHEALTH_AGENCY_RESOURCE` COMMENT '代理商资源表';


# 代理商权限表
DROP TABLE IF EXISTS `GHEALTH_AGENCY_AUTHORITY`;
CREATE TABLE `GHEALTH_AGENCY_AUTHORITY`
(
    `ID`                varchar(64)         NOT NULL        COMMENT '主键',
    `PARENT_ID`                varchar(64)         ,
    `CODE`                varchar(128)         NOT NULL UNIQUE COMMENT '权限编号' ,
     `NAME`                varchar(128)         NOT NULL   COMMENT '权限名称',
     `SORT`               integer        NOT NULL  COMMENT '排序编号',
     CONSTRAINT PK_AGENCY_AUTHORITY PRIMARY KEY (`ID`)
)
;
ALTER TABLE `GHEALTH_AGENCY_AUTHORITY` COMMENT '代理商权限资源关联表';



# 代理商权限资源关联表
DROP TABLE IF EXISTS `GHEALTH_AGENCY_AUTHORITY_RESOURCE`;
CREATE TABLE `GHEALTH_AGENCY_AUTHORITY_RESOURCE`
(
    `AUTHORITY_ID`      varchar(64)         NOT NULL,
    `RESOURCE_ID`       varchar(64)         NOT NULL
)
;
ALTER TABLE `GHEALTH_AGENCY_AUTHORITY_RESOURCE` COMMENT '代理商权限资源关联表';


# 代理商角色表
DROP TABLE IF EXISTS `GHEALTH_AGENCY_ROLE`;
CREATE TABLE `GHEALTH_AGENCY_ROLE`
(
    `ID`                varchar(64)         NOT NULL        COMMENT '主键',
    `AGENCY_ID`        varchar(64)        NOT NULL,
    `NAME`              varchar(128)        NOT NULL,
    `CREATE_TIME`       datetime            NOT NULL        COMMENT '创建时间',
    `CREATOR_NAME`      varchar(64)                         COMMENT '创建人姓名',
    `UPDATE_TIME`       datetime                            COMMENT '最近更新时间',
    `UPDATOR_NAME`      varchar(64)                         COMMENT '最近更新人姓名',
    `DELETED`           tinyint(1)          NOT NULL        COMMENT '删除标记 0-未删除 1-已删除',
    `DELETE_TIME`       datetime                            COMMENT '删除时间',
    `DELETOR_NAME`      varchar(64)                         COMMENT '删除人姓名',
    CONSTRAINT PK_AGENCY_ROLE PRIMARY KEY (`ID`)
)
;
ALTER TABLE `GHEALTH_AGENCY_ROLE` COMMENT '代理商角色表';


# 代理商角色关联表
DROP TABLE IF EXISTS `GHEALTH_AGENCY_ACCOUNT_ROLE`;
CREATE TABLE `GHEALTH_AGENCY_ACCOUNT_ROLE`
(
    `USER_ID`           varchar(64)         NOT NULL,
    `ROLE_ID`           varchar(64)         NOT NULL
)
;
ALTER TABLE `GHEALTH_AGENCY_ACCOUNT_ROLE` COMMENT '代理商角色关联表';


# 角色权限关联表
DROP TABLE IF EXISTS `GHEALTH_AGENCY_ROLE_AUTHORITY`;
CREATE TABLE `GHEALTH_AGENCY_ROLE_AUTHORITY`
(

    `ROLE_ID`           varchar(64)         NOT NULL,
    `AUTHORITY_ID`      varchar(64)         NOT NULL
)
;
ALTER TABLE `GHEALTH_AGENCY_ROLE_AUTHORITY` COMMENT '角色权限关联表';



ALTER TABLE `GHEALTH_AGENCY_ACCOUNT` ADD COLUMN `CREATE_TIME`  datetime NOT NULL DEFAULT '2017-12-27 14:02:59' COMMENT '创建时间' AFTER `PRIMARY_ACCOUNT`;
ALTER TABLE `GHEALTH_AGENCY_ACCOUNT` ADD COLUMN `CREATOR_NAME`   varchar(64)       COMMENT '创建人姓名'     AFTER `CREATE_TIME`;
ALTER TABLE `GHEALTH_AGENCY_ACCOUNT` ADD COLUMN `UPDATE_TIME`      datetime            COMMENT '最近更新时间'     AFTER `CREATOR_NAME`;
ALTER TABLE `GHEALTH_AGENCY_ACCOUNT` ADD COLUMN `UPDATOR_NAME`      varchar(64)       COMMENT '最近更新人姓名'     AFTER `UPDATE_TIME`;
ALTER TABLE `GHEALTH_AGENCY_ACCOUNT` ADD COLUMN `DELETED`      tinyint(1)          NOT NULL          COMMENT '删除标记 0-未删除 1-已删除'     AFTER `UPDATOR_NAME`;
ALTER TABLE `GHEALTH_AGENCY_ACCOUNT` ADD COLUMN `DELETE_TIME`      datetime            COMMENT '删除时间'     AFTER `DELETED`;
ALTER TABLE `GHEALTH_AGENCY_ACCOUNT` ADD COLUMN `DELETOR_NAME`    varchar(64)              COMMENT '删除人姓名'     AFTER `UPDATE_TIME`;


update `GHEALTH_AGENCY_ACCOUNT`a, `GHEALTH_AGENCY`b set a.CREATE_TIME=b.CREATE_TIME WHERE  a.AGENCY_ID = b.ID;
update `GHEALTH_AGENCY_ACCOUNT`a, `GHEALTH_AGENCY`b set a.CREATOR_NAME=b.CREATOR_NAME WHERE  a.AGENCY_ID = b.ID;
update `GHEALTH_AGENCY_ACCOUNT`a, `GHEALTH_AGENCY`b set a.UPDATE_TIME=b.UPDATE_TIME WHERE  a.AGENCY_ID = b.ID;
update `GHEALTH_AGENCY_ACCOUNT`a, `GHEALTH_AGENCY`b set a.UPDATOR_NAME=b.UPDATOR_NAME WHERE  a.AGENCY_ID = b.ID;
update `GHEALTH_AGENCY_ACCOUNT`a, `GHEALTH_AGENCY`b set a.DELETED=b.DELETED WHERE  a.AGENCY_ID = b.ID;
update `GHEALTH_AGENCY_ACCOUNT`a, `GHEALTH_AGENCY`b set a.DELETOR_NAME=b.DELETOR_NAME WHERE  a.AGENCY_ID = b.ID;



# 客户管理
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('347905a3cbac475d9ceef4bdc14d3355', NULL, 'CUSTOMER', '客户管理', 1);

INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('6f1e3a6ef4f04eaaabbae29e1062f556', '347905a3cbac475d9ceef4bdc14d3355', 'CUSTOMER.CREATE', '新增客户', 1);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('1c98f61f11ba4a949ac6d2efe2463811', '347905a3cbac475d9ceef4bdc14d3355', 'CUSTOMER.MODIFY', '修改客户', 2);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('372bd20fe5394cb787c49da01826db7f', '347905a3cbac475d9ceef4bdc14d3355', 'CUSTOMER.DELETE', '删除客户', 3);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('d85ac621e917438babf172aed6c23a0d', '347905a3cbac475d9ceef4bdc14d3355', 'CUSTOMER.DISPLAY', '查询客户', 4);

INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('a41ac62531ba492e889e0d572ae0bcbc', '新增客户', '/customer/create.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('19775e4c111f48688260f97c8bccabf4', '修改客户', '/customer/modify.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('7aa445610908437f931a5b3e05cfae0e', '删除客户', '/customer/delete.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('61a609d71d9c4cacbf6c1ba7ae598705', '查询客户', '/customer/list.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('a99effd4f499449e908aff4bf19a305f', '客户详情', '/customer/display.jsp');

INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('6f1e3a6ef4f04eaaabbae29e1062f556', 'a41ac62531ba492e889e0d572ae0bcbc');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('1c98f61f11ba4a949ac6d2efe2463811', '19775e4c111f48688260f97c8bccabf4');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('372bd20fe5394cb787c49da01826db7f', '7aa445610908437f931a5b3e05cfae0e');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('d85ac621e917438babf172aed6c23a0d', '61a609d71d9c4cacbf6c1ba7ae598705');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('d85ac621e917438babf172aed6c23a0d', 'a99effd4f499449e908aff4bf19a305f');

# 订单管理
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('069c6bfeed5a42a99516fccc5891ede4', NULL, 'ORDER', '订单管理', 2);

INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('043c8af510654e1fa6fc810dccf2d1c3', '069c6bfeed5a42a99516fccc5891ede4', 'ORDER.CREATE', '新增订单', 1);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('3bbb5ed3ab254c1aa3bb4b3003f6550d', '069c6bfeed5a42a99516fccc5891ede4', 'ORDER.EXPORT', '订单导出', 2);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('42b314a049ca4a4cabd00f9a1d125933', '069c6bfeed5a42a99516fccc5891ede4', 'ORDER.IMPORT', '订单批量导入', 3);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('7575cebe027543b89431bb35b504ce30', '069c6bfeed5a42a99516fccc5891ede4', 'ORDER.DISPLAY', '查询订单', 4);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('d66e8c9802fb4552989b59206e16e83a', '069c6bfeed5a42a99516fccc5891ede4', 'ORDER.DOWNLOAD', '报告下载', 5);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('e34344b67bf840b0b253d16147208f38', '069c6bfeed5a42a99516fccc5891ede4', 'ORDER.CANCLE', '取消订单', 6);

INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('9daef7676b6541f4b3e07fee5b11d3b2', '新增订单', '/order/create.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('25aecb6ee5984c41b69e17afa1de488b', '下单', '/order/place.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('66eacb1b838a4e688562cfea8454745f', '订单导出', '/order/downloadReports.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('cd1090fa3bb7404db67d9b1780484ed8', '订单批量导入', '/order/upload.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('7145b826b68a4c16b503379b30bf5f6c', '查询订单', '/order/list.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('91fd62e64b3443b5829734f2f49ef7b2', '订单详情', '/order/display.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('63916eb607f74e9eb229b579dcaca38d', '报告下载', '/order/report/download.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('446b586ea41547beb59ad79473d6ff7d', '取消订单', '/order/cancel.jsp');


INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('043c8af510654e1fa6fc810dccf2d1c3', '9daef7676b6541f4b3e07fee5b11d3b2');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('043c8af510654e1fa6fc810dccf2d1c3', '25aecb6ee5984c41b69e17afa1de488b');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('3bbb5ed3ab254c1aa3bb4b3003f6550d', '66eacb1b838a4e688562cfea8454745f');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('42b314a049ca4a4cabd00f9a1d125933', 'cd1090fa3bb7404db67d9b1780484ed8');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('7575cebe027543b89431bb35b504ce30', '7145b826b68a4c16b503379b30bf5f6c');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('7575cebe027543b89431bb35b504ce30', '91fd62e64b3443b5829734f2f49ef7b2');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('d66e8c9802fb4552989b59206e16e83a', '63916eb607f74e9eb229b579dcaca38d');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('e34344b67bf840b0b253d16147208f38', '446b586ea41547beb59ad79473d6ff7d');

# 代理产品
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('a19649cf650f4d5ca21d4ee4c8749425', NULL, 'PRODUCT', '代理产品', 3);

INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('957a4de487324877928d9893a0238e28', 'a19649cf650f4d5ca21d4ee4c8749425', 'PRODUCT.DISPLAY', '查询产品', 1);

INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('110a96628d3040ef951bdd1a8524c511', '查询产品', '/product/list.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('87614469f05e49c5a984b97a3f59f59c', '产品详情', '/product/display.jsp');

INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('957a4de487324877928d9893a0238e28', '110a96628d3040ef951bdd1a8524c511');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('957a4de487324877928d9893a0238e28', '87614469f05e49c5a984b97a3f59f59c');


# 账单明细
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('c61eea36c8ff45de8f6f1f6a69898928', NULL, 'BILL', '账单明细', 4);

INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('1f86c1980b4d474e9eccf5679b715aa3', 'c61eea36c8ff45de8f6f1f6a69898928', 'BILL.EXPORT', '账单导出', 1);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('3d7602085e684c939afe3094fb032f1f', 'c61eea36c8ff45de8f6f1f6a69898928', 'BILL.DISPLAY', '查询账单', 2);

INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('9496aab15df549fda8dbe37c29266456', '账单导出', '/agencyBill/agencyBillDownload.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('d8bcef210338409eaf6146d1b7ad1360', '查询账单', '/agencyBill/list.jsp');

INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('1f86c1980b4d474e9eccf5679b715aa3', '9496aab15df549fda8dbe37c29266456');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('3d7602085e684c939afe3094fb032f1f', 'd8bcef210338409eaf6146d1b7ad1360');


# 用户管理
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('3e6e4fea9b8a42b49ea77f00be1605f1', NULL, 'USER', '用户管理', 5);

INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('65da5d39b6d74850a60b6864afb89523', '3e6e4fea9b8a42b49ea77f00be1605f1', 'USER.CREATE', '新增用户', 1);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('446da6adf9d6473f96f5e1c7f1545cca', '3e6e4fea9b8a42b49ea77f00be1605f1', 'USER.MODIFY', '修改用户', 2);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('4a92a7af63274dd0a288bb5295264fba', '3e6e4fea9b8a42b49ea77f00be1605f1', 'USER.DELETE', '删除用户', 3);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('ccc23ff10ebc45cdb62d1b33806ca646', '3e6e4fea9b8a42b49ea77f00be1605f1', 'USER.DISPLAY', '查询用户', 4);

INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('100a6a76c63f47c49e01759761ae07a0', '新增用户', '/user/create.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('31ac5cd777d545aaab7551051dc8600d', '修改用户', '/user/modify.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('cf614832e2ee47e499ddbd2663f21441', '删除用户', '/user/delete.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('e621ed19c74f4c3faf0bfe372a90a58b', '查询用户', '/user/list.jsp');

INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('65da5d39b6d74850a60b6864afb89523', '100a6a76c63f47c49e01759761ae07a0');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('446da6adf9d6473f96f5e1c7f1545cca', '31ac5cd777d545aaab7551051dc8600d');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('4a92a7af63274dd0a288bb5295264fba', 'cf614832e2ee47e499ddbd2663f21441');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('ccc23ff10ebc45cdb62d1b33806ca646', 'e621ed19c74f4c3faf0bfe372a90a58b');

# 角色管理
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('9159b53dd8264e7ba2a036ea66624220', NULL, 'ROLE', '角色管理', 6);

INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('b28377498583410cabc780266d5f70cb', '9159b53dd8264e7ba2a036ea66624220', 'ROLE.CREATE', '新增角色', 1);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('7e8e039de1d3437a945ae2659aa0f77c', '9159b53dd8264e7ba2a036ea66624220', 'ROLE.MODIFY', '修改角色', 2);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('a8e253958eb7490ca31d2cced8d824e2', '9159b53dd8264e7ba2a036ea66624220', 'ROLE.DELETE', '删除角色', 3);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('89e2241491b24f95b117d4bb722f4588', '9159b53dd8264e7ba2a036ea66624220', 'ROLE.DISPLAY', '查询角色', 4);
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('3423486a2e744659a90d694a27cc02c5', '9159b53dd8264e7ba2a036ea66624220', 'ROLE.ADDUSER', '添加用户', 5);

INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('97d6cf2feacd4b568cde7f2efe611bff', '新增角色', '/role/create.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('170fe0b6650b46dabea4a12e03502a49', '修改角色', '/role/modify.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('048c18436ee44a01b3cf031a7e681b37', '删除角色', '/role/delete.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('34b46266a19243c6a9d403926692333f', '查询角色', '/role/list.jsp');
INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('faa8f3c1497c4962bd686899211eea5c', '添加用户', '/role/addUser_form.jsp');

INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('b28377498583410cabc780266d5f70cb', '97d6cf2feacd4b568cde7f2efe611bff');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('7e8e039de1d3437a945ae2659aa0f77c', '170fe0b6650b46dabea4a12e03502a49');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('a8e253958eb7490ca31d2cced8d824e2', '048c18436ee44a01b3cf031a7e681b37');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('89e2241491b24f95b117d4bb722f4588', '34b46266a19243c6a9d403926692333f');
INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('3423486a2e744659a90d694a27cc02c5', 'faa8f3c1497c4962bd686899211eea5c');


# 金额展示
INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('26a462045d0240ceb9ef3ebdd12a0032', NULL, 'MONEY', '金额展示', 7);

INSERT INTO `GHEALTH_AGENCY_AUTHORITY` (`ID`, `PARENT_ID`, `CODE`, `NAME`, `SORT`) VALUES ('99d19b043a8e48f19212aede401a8747', '26a462045d0240ceb9ef3ebdd12a0032', 'MONEY.SHOW', '金额展示', 1);

INSERT INTO `GHEALTH_AGENCY_RESOURCE` (`ID`, `NAME`, `URI`) VALUES ('885a737622ca45f7923c94a13e0a5485', '金额展示', '/money/show');

INSERT INTO `GHEALTH_AGENCY_AUTHORITY_RESOURCE` (`AUTHORITY_ID`, `RESOURCE_ID`) VALUES ('99d19b043a8e48f19212aede401a8747', '885a737622ca45f7923c94a13e0a5485');







