package zjut.edu.cn.footPrintMap.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import zjut.edu.cn.footPrintMap.common.result.Result;
import zjut.edu.cn.footPrintMap.common.result.ResultStatus;
import zjut.edu.cn.footPrintMap.dto.request.friendRequset.HandleRequset;
import zjut.edu.cn.footPrintMap.dto.response.friendResponse.FriendVO;
import zjut.edu.cn.footPrintMap.entity.Friend;
import zjut.edu.cn.footPrintMap.entity.User;
import zjut.edu.cn.footPrintMap.service.FriendService;
import zjut.edu.cn.footPrintMap.service.UserService;

import java.util.ArrayList;
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
        User friendUser = userService.getUserByUsername(friendUsername);
        if(friendUser == null) {
            return Result.error(ResultStatus.NOT_FOUND, "用户不存在");
        }
        boolean exists = friendService.exists(user.getUsername(), friendUsername);
        if(exists) {
            return Result.error(ResultStatus.PARAM_ERROR, "好友请求已存在");
        }
        Friend friend = new Friend();
        friend.setUserId(user.getId());
        friend.setFriendId(friendUser.getId());
        friend.setStatus(0);
        boolean saved = friendService.save(friend);
        return saved ? Result.success(null) : Result.error(ResultStatus.USE_FAILED);
    }

    //处理好友请求（同意/拒绝/删除）
    @PutMapping("/handle")
    public Result<Void> handleRequest(@RequestBody HandleRequset handleRequset) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        User friendUser = userService.getUserByUsername(handleRequset.getFriendUsername());
        if(friendUser == null) {
            return Result.error(ResultStatus.NOT_FOUND, "用户不存在");
        }

        // 修复：处理请求时，当前用户是"接收者"（friendId = me），
        // 原来查 userId = me 是反的，导致永远找不到记录
        Friend friend = friendService
                .lambdaQuery()
                .eq(Friend::getFriendId, user.getId())
                .eq(Friend::getUserId, friendUser.getId())
                .one();

        // 如果作为接收者查不到，再尝试作为发送者查（兼容删除已通过好友的场景）
        if(friend == null) {
            friend = friendService
                    .lambdaQuery()
                    .eq(Friend::getUserId, user.getId())
                    .eq(Friend::getFriendId, friendUser.getId())
                    .one();
        }
        if(friend == null) {
            return Result.error(ResultStatus.NOT_FOUND);
        }

        if(handleRequset.getStatus() == 3) {
            //删除好友
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

    //获取好友列表（含好友用户信息，双向查询）
    @GetMapping("/friendList")
    public Result<List<FriendVO>> getFriendList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());

        // 双向查询：我发出的请求 (userId=me) + 别人发给我的请求 (friendId=me)
        List<Friend> allFriends = friendService.list(new LambdaQueryWrapper<Friend>()
                .and(w -> w.eq(Friend::getUserId, user.getId())
                           .or()
                           .eq(Friend::getFriendId, user.getId()))
                .eq(Friend::getStatus, 1));

        List<FriendVO> voList = new ArrayList<>();
        for(Friend f : allFriends) {
            // 找到"对方"的 userId（不是我自己的）
            String otherUserId = f.getUserId().equals(user.getId()) ? f.getFriendId() : f.getUserId();
            User friendUser = userService.getById(otherUserId);
            if(friendUser == null) continue;

            FriendVO vo = new FriendVO();
            vo.setId(f.getId());
            vo.setFriendId(friendUser.getId());
            vo.setUsername(friendUser.getUsername());
            vo.setNickname(friendUser.getNickname());
            vo.setAvatar(friendUser.getAvatar());
            vo.setBio(friendUser.getBio());
            vo.setStatus(f.getStatus());
            vo.setCreatedTime(f.getCreatedTime());
            voList.add(vo);
        }
        return Result.success(voList);
    }

    //获取待处理的好友请求（我作为接收者，status=0）
    @GetMapping("/pending")
    public Result<List<FriendVO>> getPendingRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());

        // 查询别人发给我的、待验证的请求
        List<Friend> pendingList = friendService.list(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getFriendId, user.getId())
                .eq(Friend::getStatus, 0)
                .orderByDesc(Friend::getCreatedTime));

        List<FriendVO> voList = new ArrayList<>();
        for(Friend f : pendingList) {
            User sender = userService.getById(f.getUserId());
            if(sender == null) continue;

            FriendVO vo = new FriendVO();
            vo.setId(f.getId());
            vo.setFriendId(sender.getId());
            vo.setUsername(sender.getUsername());
            vo.setNickname(sender.getNickname());
            vo.setAvatar(sender.getAvatar());
            vo.setBio(sender.getBio());
            vo.setStatus(f.getStatus());
            vo.setCreatedTime(f.getCreatedTime());
            voList.add(vo);
        }
        return Result.success(voList);
    }

    //获取指定用户的基本信息（点击好友头像查看）
    @GetMapping("/userInfo")
    public Result<User> getUserInfo(@RequestParam String userId) {
        User user = userService.getById(userId);
        if(user == null) {
            return Result.error(ResultStatus.NOT_FOUND, "用户不存在");
        }
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }
}
