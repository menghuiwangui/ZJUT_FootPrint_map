package zjut.edu.cn.footPrintMap.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
//import tools.jackson.databind.util.BeanUtil;
import zjut.edu.cn.footPrintMap.common.result.Result;
import zjut.edu.cn.footPrintMap.common.result.ResultStatus;
import zjut.edu.cn.footPrintMap.common.utils.JWTUtil;
import zjut.edu.cn.footPrintMap.dto.request.userRequest.LoginRequest;
import zjut.edu.cn.footPrintMap.dto.request.userRequest.RegisterRequest;
import zjut.edu.cn.footPrintMap.dto.request.userRequest.UpdatePasswordRequest;
import zjut.edu.cn.footPrintMap.dto.request.userRequest.UpdateUserRequest;
import zjut.edu.cn.footPrintMap.dto.response.userResponse.LoginResponse;
import zjut.edu.cn.footPrintMap.entity.User;
import zjut.edu.cn.footPrintMap.mapper.UserDetailsMapper;
import zjut.edu.cn.footPrintMap.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

//        User mockUser = new User();
//        mockUser.setId("1");
//        mockUser.setUsername(loginRequest.getUsername());
//        mockUser.setRole(0);
//
//        String token = jwtUtil.createToken(mockUser);
//        LoginResponse response = new LoginResponse(token, mockUser.getUsername(), mockUser.getRole());
//        return Result.success(response);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        //认证成功后拿到用户信息
        UserDetailsMapper userDetailsMapper = (UserDetailsMapper) authentication.getPrincipal();
        if (userDetailsMapper != null) {
            String user_id = userDetailsMapper.getId();
            String token = jwtUtil.createToken(userService.getById(user_id));
            LoginResponse response = new LoginResponse(
                    token,
                    userDetailsMapper.getUsername(),
                    userDetailsMapper.getRole());
            return Result.success(response);
        }
        return Result.error(ResultStatus.PARAM_ERROR);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest registerRequest) {

//        return Result.success(null);
        boolean exists = userService.lambdaQuery()
                .eq(User::getUsername,registerRequest.getUsername()).exists();
        if(exists) {
            return Result.error(ResultStatus.PARAM_ERROR,"用户名已存在");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setNickname(registerRequest.getNickname());
        boolean saved = userService.save(user);
        return saved ? Result.success(null) : Result.error(ResultStatus.USE_FAILED);
    }

    @GetMapping("/currentUser")
    public Result<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        if(user == null) {
            return Result.error(ResultStatus.NOT_FOUND,"用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/currentUser")
    public Result<String> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        if(user == null) {
            return Result.error(ResultStatus.NOT_FOUND,"用户不存在");
        }
        BeanUtils.copyProperties(updateUserRequest,user);
        boolean updated = userService.updateById(user);
        return updated ? Result.success("用户信息更新成功") : Result.error(ResultStatus.USE_FAILED);
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        if(user == null) {
            return Result.error(ResultStatus.NOT_FOUND,"用户不存在");
        }
        if(!passwordEncoder.matches(updatePasswordRequest.getOldPassword(),user.getPassword())) {
            return Result.error(ResultStatus.PARAM_ERROR, "旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        boolean updated = userService.updateById(user);
        return updated ? Result.success(null) : Result.error(ResultStatus.USE_FAILED);
    }
    @DeleteMapping("/currentUser")
    public Result<Void> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        if(user == null) {
            return Result.error(ResultStatus.NOT_FOUND,"用户不存在");
        }
        boolean removed = userService.removeById(user.getId());
        return removed ? Result.success(null) : Result.error(ResultStatus.NOT_FOUND);
    }
}
