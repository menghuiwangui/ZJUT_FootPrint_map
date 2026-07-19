package zjut.edu.cn.footPrintMap.dto.response.friendResponse;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendVO {
    private Integer id;
    private String friendId;
    private String username;
    private String nickname;
    private String avatar;
    private String bio;
    private Integer status;
    private LocalDateTime createdTime;
}
