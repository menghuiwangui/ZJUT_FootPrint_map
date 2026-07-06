package zjut.edu.cn.footPrintMap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zjut.edu.cn.footPrintMap.entity.Friend;
import zjut.edu.cn.footPrintMap.mapper.FriendMapper;
import zjut.edu.cn.footPrintMap.service.FriendService;
import zjut.edu.cn.footPrintMap.service.UserService;

@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Autowired
    private UserService userService;

    @Override
    public boolean exists(String userName,String friendName) {
        String userId = userService.getUserByUsername(userName).getId();
        String friendUserId = userService.getUserByUsername(friendName).getId();
        return exists(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getUserId,userId)
                .eq(Friend::getFriendId,friendUserId));
    }
}
