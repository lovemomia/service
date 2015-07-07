CREATE TABLE `t_admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(50) NOT NULL COMMENT '用户密码',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Admin账户';

CREATE TABLE `t_banner` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `cityId` int(11) NOT NULL COMMENT '城市ID',
  `cover` varchar(250) NOT NULL COMMENT '封面图',
  `action` varchar(250) NOT NULL COMMENT 'Action',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Banner';

CREATE TABLE `t_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `name` varchar(50) NOT NULL COMMENT '类别名称',
  `parentId` int(11) NOT NULL COMMENT '父类别ID',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='类别';

CREATE TABLE `t_city` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `name` varchar(250) NOT NULL COMMENT '城市名称',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='城市';

CREATE TABLE `t_feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id编号',
  `content` varchar(200) NOT NULL COMMENT '反馈内容',
  `email` varchar(100) NOT NULL COMMENT '用户邮箱',
  `userId` bigint(20) NOT NULL DEFAULT '0' COMMENT '反馈人',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '反馈时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户反馈';

CREATE TABLE `t_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `customerId` bigint(20) NOT NULL COMMENT '客户ID',
  `productId` bigint(20) NOT NULL COMMENT '产品ID',
  `skuId` bigint(20) NOT NULL COMMENT 'SKU ID',
  `prices` varchar(500) NOT NULL COMMENT '价格数量',
  `contacts` varchar(50) NOT NULL DEFAULT '' COMMENT '联系人',
  `mobile` varchar(20) NOT NULL COMMENT '联系电话',
  `participants` varchar(100) NOT NULL COMMENT '参与人员ID',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单';

CREATE TABLE `t_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `orderId` bigint(20) NOT NULL COMMENT '订单ID',
  `payer` varchar(128) NOT NULL COMMENT '付款人信息',
  `finishTime` datetime NOT NULL COMMENT '支付完成时间',
  `payType` int(11) NOT NULL COMMENT '支付类型',
  `tradeNo` varchar(64) NOT NULL COMMENT '交易号',
  `fee` decimal(8,2) NOT NULL COMMENT '支付金额',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付单';

CREATE TABLE `t_place` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `cityId` int(11) NOT NULL COMMENT '城市ID',
  `regionId` int(11) NOT NULL COMMENT '区域ID',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `address` varchar(250) NOT NULL COMMENT '地址',
  `desc` varchar(500) NOT NULL DEFAULT '' COMMENT '描述',
  `lng` double NOT NULL COMMENT '经度',
  `lat` double NOT NULL COMMENT '纬度',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='地点';

CREATE TABLE `t_place_img` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `placeId` bigint(20) NOT NULL COMMENT '地点ID',
  `url` varchar(250) NOT NULL DEFAULT '' COMMENT '图片URL',
  `width` int(11) NOT NULL COMMENT '图片宽度',
  `height` int(11) NOT NULL COMMENT '图片高度',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='地点图片';

CREATE TABLE `t_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `cityId` int(11) NOT NULL COMMENT '所在城市',
  `categoryId` int(11) NOT NULL COMMENT '类别',
  `title` varchar(50) NOT NULL COMMENT '标题',
  `cover` varchar(250) NOT NULL COMMENT '封面图片',
  `crowd` varchar(50) NOT NULL COMMENT '适合人群',
  `placeId` int(11) NOT NULL COMMENT '地点ID',
  `content` mediumtext NOT NULL COMMENT '内容',
  `sales` int(11) NOT NULL COMMENT '销量',
  `startTime` datetime NOT NULL COMMENT '开始时间',
  `endTime` datetime NOT NULL COMMENT '结束时间',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='产品';

CREATE TABLE `t_product_img` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `productId` bigint(20) NOT NULL COMMENT '产品ID',
  `url` varchar(250) NOT NULL COMMENT '图片URL',
  `width` int(11) NOT NULL COMMENT '图片宽度',
  `height` int(11) NOT NULL COMMENT '图片高度',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='产品图片';

CREATE TABLE `t_region` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `cityId` int(11) NOT NULL COMMENT '城市ID',
  `name` varchar(50) NOT NULL COMMENT '区域名称',
  `parentId` int(11) NOT NULL COMMENT '父区域ID',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='地区';

CREATE TABLE `t_secret` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `biz` varchar(20) NOT NULL COMMENT '业务名',
  `key` varchar(256) NOT NULL COMMENT 'Key',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='密钥';

CREATE TABLE `t_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `productId` int(11) NOT NULL COMMENT '产品ID',
  `properties` varchar(500) NOT NULL COMMENT '属性',
  `prices` varchar(1000) NOT NULL COMMENT '价格信息',
  `limit` int(11) NOT NULL DEFAULT 0 COMMENT '限额',
  `needRealName` int(1) NOT NULL DEFAULT 0 COMMENT '是否实名',
  `stock` int(11) NOT NULL COMMENT '库存',
  `unlockedStock` int(11) NOT NULL COMMENT '未锁定的库存',
  `lockedStock` int(11) NOT NULL COMMENT '锁定的库存',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='产品SKU';

CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `token` varchar(100) NOT NULL COMMENT '用户token',
  `mobile` varchar(15) NOT NULL COMMENT '手机号',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `nickName` varchar(100) NOT NULL COMMENT '用户昵称',
  `avatar` varchar(256) DEFAULT '' COMMENT '用户头像',
  `name` varchar(20) DEFAULT '' COMMENT '用户名字',
  `sex` varchar(10) DEFAULT '未知' COMMENT '用户性别',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `cityId` int(11) DEFAULT '0' COMMENT '城市ID',
  `address` varchar(500) DEFAULT '' COMMENT '用户地址',
  `children` varchar(500) DEFAULT '' COMMENT '孩子',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户';

CREATE TABLE `t_user_participant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(20) NOT NULL COMMENT '参与人员名字',
  `sex` varchar(10) NOT NULL COMMENT '参与人员性别',
  `birthday` date NOT NULL COMMENT '参与人员生日',
  `idType` int(11) DEFAULT '0' COMMENT '证件类型',
  `idNo` varchar(64) DEFAULT '' COMMENT '证件号码',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `addTime` datetime NOT NULL COMMENT '添加时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='参与人员';

CREATE TABLE `t_verify` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id编号',
  `mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号',
  `code` varchar(6) NOT NULL DEFAULT '' COMMENT '验证码',
  `generateTime` datetime NOT NULL COMMENT '生成时间',
  `sendTime` datetime DEFAULT NULL COMMENT '发送时间',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态,0为删除',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='验证码';

