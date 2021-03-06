ALTER TABLE `GHEALTH_CUSTOMER` ADD COLUMN `NATION`                  varchar(64)  COMMENT '民族'        AFTER `BIRTHDAY`;
ALTER TABLE `GHEALTH_CUSTOMER` ADD COLUMN `MARITAL_STATUS`          varchar(64)  COMMENT '婚姻状况'     AFTER `NATION`;
ALTER TABLE `GHEALTH_CUSTOMER` ADD COLUMN `VOCATION`                varchar(64)  COMMENT '职业'        AFTER `MARITAL_STATUS`;
ALTER TABLE `GHEALTH_CUSTOMER` ADD COLUMN `COMPANY`                 varchar(64)  COMMENT '工作单位'     AFTER `VOCATION`;

ALTER TABLE `GHEALTH_AGENCY` ADD COLUMN `PREAUTHORIZED_AMOUNT`      decimal(10, 2)  COMMENT '预授权金额'     AFTER `REMARK`;





INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`, `EDITABLE`) VALUES ('6016311cdefe11e7b5cd3d5e1e7d1b10', NULL, 'MARITAL_STATUS', '婚姻状况', NULL, NULL, '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`, `EDITABLE`) VALUES ('612610ecdefe11e7b5cd3d5e1e7d1b10', '6016311cdefe11e7b5cd3d5e1e7d1b10', 'MARITAL_STATUS', '未婚', '0', '1', '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`, `EDITABLE`) VALUES ('6132738edefe11e7b5cd3d5e1e7d1b10', '6016311cdefe11e7b5cd3d5e1e7d1b10', 'MARITAL_STATUS', '已婚', '1', '2', '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`, `EDITABLE`) VALUES ('6148c8a6defe11e7b5cd3d5e1e7d1b10', '6016311cdefe11e7b5cd3d5e1e7d1b10', 'MARITAL_STATUS', '离异', '2', '3', '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`, `EDITABLE`) VALUES ('61546b7adefe11e7b5cd3d5e1e7d1b10', '6016311cdefe11e7b5cd3d5e1e7d1b10', 'MARITAL_STATUS', '丧偶', '3', '4', '0');

INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('0238daa8deff11e7b5cd3d5e1e7d1b10', NULL, 'BASE_NATION', '民族', NULL, NULL, '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('1330a79d129946958a3c5d60b87eba5f', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '汉族', '0', '0',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('517fe1a0a2b647faaefad512cc826e25', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '回族', '1', '1',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('f528925e92924b5291d89f6f5e7f21c5', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '藏族', '2', '2',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('09ac904c44034902ad5580dc9bb7ba96', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '维吾尔族', '3', '3',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('7da77558bc4d4ef7a549fbad70e06661', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '苗族', '4', '4',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('d1fc83cdfc544ce0bef03ac861cf7fe1', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '彝族', '5', '5',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('180a392a453f47fb8a7a663001f6fd43', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '壮族', '6', '6',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('16957cf38f8b479bb8c8ef712dc09619', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '布依族', '7', '7',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('b9b5c30324c44a3a816665048ba7f0ca', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '蒙古族', '8', '8',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('267a30c91b2c416ca92bdc20a71bea5e', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '朝鲜族', '9', '9',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('e520aa0e53614c1caf9d04276c9da08f', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '满族', '10', '10',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('e87610fff068489e92d80723c70f25ea', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '侗族', '11', '11',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('2796b4decfd44b5e94693559bcac6944', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '瑶族', '12', '12',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('9789616736bb485aadee8f5741f093fe', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '白族', '13', '13',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('b423ffc7cd8a4ae590a68a1d7b5e0ac7', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '土家族', '14', '14',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('ed8f9e0325044209bb4792ab48811b98', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '哈尼族', '15', '15',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('ddb6afa103184851babc598a0e2df9cd', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '哈萨克族', '16', '16',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('fa739dc4584f43ca914c2067c764a6ec', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '傣族', '17', '17',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('20d7a10fa0d84450b036721f0d160c05', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '黎族', '18', '18',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('5770477a627544979e6f0ee0d3fc19f8', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '傈僳族', '19', '19',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('c0a374ded8a34360b34fef8db01e5628', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '佤族', '20', '20',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('b6c067a896424c85aa7acb12e677f924', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '畲族', '21', '21',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('7a0371f1f2e24755bb5035b5e8d9ffd5', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '高山族', '22', '22',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('dfb1f9d25bd94573b11d366902ea047f', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '拉祜族', '23', '23',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('ca7ce177c3d4446ca44b38c1716ec092', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '水族', '24', '24',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('3c9aafdddb604c7881e0dc064c1bf300', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '东乡族', '25', '25',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('da81b548bba8440c82be1546a712b515', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '纳西族', '26', '26',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('c3bc723cb7214075aad092c6de6afc29', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '景颇族', '27', '27',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('c9d7e3ce9a904f8fba525108d56350d5', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '柯尔克孜族', '28', '28',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('83a5d2b226544622b20b8fea7f81d79d', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '土族', '29', '29',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('b2d4d00f83d8481a801e6065d87daae2', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '达斡尔族', '30', '30',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('d49f42429cd74e5ba62d51754981e947', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '仫佬族', '31', '31',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('6020c7504a634a15a7bad90d278fa72e', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '锡伯族', '32', '32',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('387adc1dd0c04396a9c7debaf62aaa2b', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '阿昌族', '33', '33',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('632d91411d354e38a7bf27640e8e5594', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '普米族', '34', '34',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('609222b0fbaf4e9691a1b35532f4ea9d', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '塔吉克族', '35', '35',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('55c24c99a61c4b9eb0f402d4ecd15a9a', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '怒族', '36', '36',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('9597b7c2e6e949a99cf3c8c6a339fc4e', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '乌孜别克族', '37', '37',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('cc477fb59020460cbc0479083c130664', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '俄罗斯族', '38', '38',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('1b85f25519a540898f62d8f46dac4d6b', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '鄂温克族', '39', '39',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('590e857595cf462ba3455bedf3b95ff4', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '德昂族', '40', '40',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('ab02232f943341188ee5aff1818ea080', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '保安族', '41', '41',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('ccff7ad93f25406297c62f7574b908c7', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '裕固族', '42', '42',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('cef02f6f77dc4d1a980f605f18682795', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '京族', '43', '43',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('3aba77ecf3e04d39902a83ba15be4e5e', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '塔塔尔族', '44', '44',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('65cfc9cc8d1b47c2a1c29fa17610ace4', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '独龙族', '45', '45',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('c22e3e143fa147b58256ed0ccf92caa0', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '鄂伦春族', '46', '46',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('f92943df19984982aa7cf11e0ff1e7f4', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '赫哲族', '47', '47',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('96962f13968c45efb3ddcd90aee65363', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '门巴族', '48', '48',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('b55a8d7401fc4980a7bcfaf5c2018a02', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '珞巴族', '49', '49',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('8b5a58a94a244b5a822be169ff298180', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '基诺族', '50', '50',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('5d770085470d449d89c864bd532c675c', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '仡佬族', '51', '51',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('c242024452f34a9db5d92a70a406c0b8', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '羌族', '52', '52',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('46601ec4a502467b9fce27e9f24a13a0', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '毛南族', '53', '53',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('da604b8e6f294ddfba281dcc35f10a7a', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '撒拉族', '54', '54',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('2d427152088c443396cca6983f79accb', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '布朗族', '55', '55',  '0');
INSERT INTO `GHEALTH_DICT` (`ID`, `PARENT_ID`, `CATEGORY`, `DICT_TEXT`, `DICT_VALUE`, `SORT`,  `EDITABLE`) VALUES ('514e41ce349945e5a01d40dfa95c714e', '0238daa8deff11e7b5cd3d5e1e7d1b10', 'BASE_NATION', '秦族', '56', '56',  '0');
