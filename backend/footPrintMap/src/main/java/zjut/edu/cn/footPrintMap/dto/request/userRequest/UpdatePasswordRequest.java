package zjut.edu.cn.footPrintMap.dto.request.userRequest;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
