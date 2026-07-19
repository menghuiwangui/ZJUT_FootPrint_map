package zjut.edu.cn.footPrintMap.dto.request.friendRequset;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HandleRequset {
    private String friendUsername;
    private Integer status;  //1-同意 2-拒绝 3-删除

    public HandleRequset(String friendUsername,Integer status) {
        this.friendUsername = friendUsername;
        this.status = status;
    }
}
