package zjut.edu.cn.footPrintMap.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import zjut.edu.cn.footPrintMap.common.result.Result;
import zjut.edu.cn.footPrintMap.common.result.ResultStatus;
import zjut.edu.cn.footPrintMap.dto.request.friendRequset.HandleRequset;
import zjut.edu.cn.footPrintMap.entity.Friend;
import zjut.edu.cn.footPrintMap.entity.User;
import zjut.edu.cn.footPrintMap.service.FriendService;
import zjut.edu.cn.footPrintMap.service.UserService;

import java.util.List;

@RestController
@RequestMapping("api/friend")
public class FriendController {
    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    //发送好友请求
    @PostMapping("/request")
    public Result<Void> sendFriendRequest(@RequestParam String friendUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        if(user.getUsername().equals(friendUsername)) {
            return Result.error(ResultStatus.PARAM_ERROR, "不能添加自己为好友");
        }
        boolean exists = friendService.exists(user.getUsername(),friendUsername);
        if(exists) {
            return Result.error(ResultStatus.PARAM_ERROR, "好友请求已存在");
        }
        Friend friend = new Friend();
        friend.setUserId(user.getId());
        friend.setFriendId(userService.getUserByUsername(friendUsername).getId());
        friend.setStatus(0);
        friendService.save(friend);
        return Result.success(null);
    }

    //处理好友状态
    @PutMapping("/handle")
    public Result<Void> handleRequest(@RequestBody HandleRequset handleRequset) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        Friend friend = friendService
                .lambdaQuery()
                .eq(Friend::getUserId,user.getId())
                .eq(Friend::getFriendId,userService.getUserByUsername(handleRequset.getFriendUsername()).getId()).one();
        if(friend == null) {
            return Result.error(ResultStatus.NOT_FOUND);
        }
        if(handleRequset.getStatus() == 3) {
            //删除
            friendService.removeById(friend);
            return Result.success(null);
        } else if (handleRequset.getStatus() == 1 || handleRequset.getStatus() == 2) {
            //同意/拒绝
            friend.setStatus(handleRequset.getStatus());
            friendService.updateById(friend);
            return Result.success(null);
        }
        return Result.error(ResultStatus.PARAM_ERROR);
    }

    //获取好友列表
    @GetMapping("/friendList")
    public Result<List<Friend>> getFriendList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        return Result.success(friendService
                .list(new LambdaQueryWrapper<Friend>()
                        .eq(Friend::getUserId,user.getId())
                        .eq(Friend::getStatus,1)));
    }

}
