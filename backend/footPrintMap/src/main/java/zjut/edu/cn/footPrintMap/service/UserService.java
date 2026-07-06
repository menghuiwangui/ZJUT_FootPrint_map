package zjut.edu.cn.footPrintMap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zjut.edu.cn.footPrintMap.entity.User;

public interface UserService extends IService<User> {
    User getUserByUsername(String username);
}
