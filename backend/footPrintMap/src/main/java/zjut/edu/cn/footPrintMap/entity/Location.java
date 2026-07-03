package zjut.edu.cn.footPrintMap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("location")
public class Location {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;
    private String name;
    private BigDecimal lat;
    private BigDecimal lng;
    private String continent;
    private String country;
    private String province;
    private String address;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
}