package zjut.edu.cn.footPrintMap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(value = "id", type = IdType.ASSIGN_UUID) // MySQL varchar(36) 用 UUID 最合适
    private String id;

    private String username;
    private String nickname;
    private String password;
    private String avatar;
    private String bio;
    private String email;
    private String phone;
    private Integer role;       // 0-普通用户，1-管理员
    private Integer status;     // 0-禁用，1-正常
    private LocalDateTime createTime;   // 数据库 DEFAULT CURRENT_TIMESTAMP，插入时可不传
    private LocalDateTime updateTime;   // 数据库 ON UPDATE CURRENT_TIMESTAMP，更新时可不传
}