package zjut.edu.cn.footPrintMap.service.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zjut.edu.cn.footPrintMap.entity.User;
import zjut.edu.cn.footPrintMap.mapper.UserDetailsMapper;
import zjut.edu.cn.footPrintMap.service.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        //开启 Lambda 查询构造器
        User user = userService.lambdaQuery().eq(User::getUsername,username).one();
        if(user == null) {
            throw new UsernameNotFoundException("用户不存在："+username);
        }
        return UserDetailsMapper.build(user);
    }
}
