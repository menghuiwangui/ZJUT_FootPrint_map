package zjut.edu.cn.footPrintMap.dto.request.friendRequset;

import lombok.Data;

@Data
public class HandleRequset {
    private String friendUsername;
    private Integer status;  //3-删除

    public HandleRequset(String friendUsername,Integer status) {
        this.friendUsername = friendUsername;
        this.status = status;
    }
}
