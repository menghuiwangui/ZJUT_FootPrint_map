package zjut.edu.cn.footPrintMap.dto.request.userRequest;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String nickname;
    private String avatar;
    private String bio;
    private String email;
    private String phone;

    public UpdateUserRequest(String nickname,String avatar,String bio,String email,String phone) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.bio = bio;
        this.email = email;
        this.phone = phone;
    }
}
