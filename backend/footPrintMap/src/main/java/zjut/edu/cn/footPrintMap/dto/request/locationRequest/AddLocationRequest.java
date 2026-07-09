package zjut.edu.cn.footPrintMap.dto.request.locationRequest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddLocationRequest {
    private String locationName;
    private BigDecimal lat;
    private BigDecimal lng;
    private String continent;
    private String country;
    private String province;
    private String address;

    public AddLocationRequest(String locationName,BigDecimal lat,BigDecimal lng,String continent,String country,String province,String address) {
        this.locationName = locationName;
        this.lat = lat;
        this.lng = lng;
        this.continent = continent;
        this.country = country;
        this.province = province;
        this.address = address;
    }
}
