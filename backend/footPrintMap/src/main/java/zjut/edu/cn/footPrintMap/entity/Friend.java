package zjut.edu.cn.footPrintMap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("friend")
public class Friend {
    @TableId(value = "id", type = IdType.AUTO) // INT AUTO_INCREMENT
    private Integer id;

    private String userId;
    private String friendId;
    private Integer status;     // 0-待验证  1-已通过  2-已拒绝
    private LocalDateTime createdTime;
}