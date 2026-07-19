package zjut.edu.cn.footPrintMap.dto.request.userRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
