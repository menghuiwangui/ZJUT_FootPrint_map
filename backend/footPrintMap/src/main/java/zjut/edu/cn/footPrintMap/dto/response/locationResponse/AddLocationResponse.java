package zjut.edu.cn.footPrintMap.dto.response.locationResponse;

import lombok.Data;

@Data
public class AddLocationResponse {
    private String locationId;
    private String locationName;

    public AddLocationResponse(String locationId,String locationName) {
        this.locationId = locationId;
        this.locationName = locationName;
    }
}
