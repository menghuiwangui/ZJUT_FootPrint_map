package zjut.edu.cn.footPrintMap.dto.response.travelResponse;

import lombok.Data;

@Data
public class AddTravelResponse {
    private String travelId;
    public AddTravelResponse(String travelId) {
        this.travelId = travelId;
    }
}
