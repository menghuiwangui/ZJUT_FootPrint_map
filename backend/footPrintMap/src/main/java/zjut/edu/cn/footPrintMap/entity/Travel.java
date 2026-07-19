package zjut.edu.cn.footPrintMap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "travel", autoResultMap = true)
public class Travel {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;
    private String locationId;
    private String title;
    private String content;

    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    private LocalDate visitTime;
    private Integer visibility;     // 0-公开  1-仅自己  2-仅好友
    private Integer likesCount;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
}