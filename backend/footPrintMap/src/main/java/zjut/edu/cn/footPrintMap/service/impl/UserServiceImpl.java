package zjut.edu.cn.footPrintMap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zjut.edu.cn.footPrintMap.entity.User;
import zjut.edu.cn.footPrintMap.mapper.UserMapper;
import zjut.edu.cn.footPrintMap.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getUserByUsername(String username) {
        return lambdaQuery().eq(User::getUsername,username).one();
    }
}
