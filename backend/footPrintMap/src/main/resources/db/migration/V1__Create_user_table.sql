CREATE TABLE user(
                     id varchar(36) PRIMARY KEY COMMENT '用户唯一UUID主键',
                     username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，登录账号，唯一',
                     nickname VARCHAR(50) NOT NULL COMMENT '用户昵称',
                     password VARCHAR(100) NOT NULL COMMENT '密码',
                     avatar VARCHAR(255) DEFAULT '' COMMENT '头像图片URL',
                     bio VARCHAR(200) DEFAULT '' COMMENT '个人简介',
                     email VARCHAR(100) DEFAULT '' COMMENT '邮箱',
                     phone VARCHAR(20) DEFAULT '' COMMENT '手机号',
                     role INT NOT NULL DEFAULT 0 COMMENT '是否管理员：0-否，1-是',
                     status INT DEFAULT 1 COMMENT '账号状态：0-禁用 1-正常',
                     create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                     update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
