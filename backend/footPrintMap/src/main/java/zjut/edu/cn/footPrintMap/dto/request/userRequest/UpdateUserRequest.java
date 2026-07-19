package zjut.edu.cn.footPrintMap.dto.request.userRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserRequest {
    private String nickname;
    private String avatar;
    private String bio;
    private String email;
    private String phone;
}
