package zjut.edu.cn.footPrintMap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zjut.edu.cn.footPrintMap.entity.Friend;

public interface FriendService extends IService<Friend> {
    boolean exists(String userName,String friendName);
}
