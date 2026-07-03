CREATE TABLE friend (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID,关联用户表user.id',
    friend_id VARCHAR(36) NOT NULL COMMENT '好友ID,关联用户表user.id',
    status INT DEFAULT 0 COMMENT '状态：0-待验证  1-已通过  2-已拒绝',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_pair (user_id,friend_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (friend_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友关系表';