package zjut.edu.cn.footPrintMap.dto.request.travelRequest;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateTravelRequest {
    private String travelId;
    private String locationId;
    private String title;
    private String content;
    private List<String> tags;
    private LocalDate visitTime;
    private Integer visibility;
    private Integer likesCount;

    //@RequestBody 通过new（无参） + getter方法反序列化
}
