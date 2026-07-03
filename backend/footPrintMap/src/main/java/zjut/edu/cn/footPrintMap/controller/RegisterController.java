package zjut.edu.cn.footPrintMap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zjut.edu.cn.footPrintMap.service.UserService;

@RestController
@RequestMapping("/api/register")
public class RegisterController {
    @Autowired
    private UserService userService;
}
