package zjut.edu.cn.footPrintMap.dto.request.travelRequest;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AddTravelRequest {
    private String locationId;
    private String title;
    private String content;
    private List<String> tags;
    private LocalDateTime visitTime;
    private Integer visibility; // 0-公开 1-仅自己 2-仅好友

    public AddTravelRequest(String locationId, String title, String content, List<String> tags, LocalDateTime visitTime, Integer visibility) {
        this.locationId = locationId;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.visitTime = visitTime;
        this.visibility = visibility;
    }
}
