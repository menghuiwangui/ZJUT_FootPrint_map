package zjut.edu.cn.footPrintMap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("travel_image")
public class TravelImage {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String travelId;
    private String url;
    private Integer sortOrder;
    private LocalDateTime createdTime;
}