package zjut.edu.cn.footPrintMap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zjut.edu.cn.footPrintMap.entity.Friend;
import zjut.edu.cn.footPrintMap.mapper.FriendMapper;
import zjut.edu.cn.footPrintMap.service.FriendService;

@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {
}
