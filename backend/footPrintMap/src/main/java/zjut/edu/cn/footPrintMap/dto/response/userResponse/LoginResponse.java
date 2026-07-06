package zjut.edu.cn.footPrintMap.dto.response.userResponse;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;      // JWT 令牌
    private String username;   // 用户名
    private Integer role;    // 角色

    public LoginResponse(String token, String username, Integer role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
}
