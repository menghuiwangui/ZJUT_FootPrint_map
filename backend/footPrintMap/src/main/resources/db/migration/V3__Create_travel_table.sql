CREATE TABLE travel (
    id VARCHAR(36) PRIMARY KEY COMMENT '游记UUID',
    user_id VARCHAR(36) NOT NULL COMMENT '关联用户表user.id',
    location_id VARCHAR(36) NOT NULL COMMENT '关联地点表location.id',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NULL COMMENT '正文内容',
    tags JSON COMMENT '标签数组',
    visit_time DATETIME NOT NULL COMMENT '到访日期',
    visibility INT DEFAULT 0 COMMENT '可见性：0-公开  1-仅自己  2-仅好友',
    likes_count INT DEFAULT 0 COMMENT '点赞数',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (location_id) REFERENCES location(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游记表';