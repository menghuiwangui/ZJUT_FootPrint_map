package zjut.edu.cn.footPrintMap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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
        boolean exists = userService.lambdaQuery()
                .eq(User::getUsername,registerRequest.getUsername()).exists();
        if(exists) {
            return Result.error(ResultStatus.PARAM_ERROR,"用户名已存在");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPassword(registerRequest.getPassword());
        user.setNickname(registerRequest.getNickname());
        userService.save(user);
        return Result.success(null);
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
    public Result<Void> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        if(user == null) {
            return Result.error(ResultStatus.NOT_FOUND,"用户不存在");
        }
        if(updateUserRequest.getNickname() != null) {
            user.setNickname(updateUserRequest.getNickname());
        }
        if (updateUserRequest.getAvatar() != null) {
            user.setAvatar(updateUserRequest.getAvatar());
        }
        if (updateUserRequest.getBio() != null) {
            user.setBio(updateUserRequest.getBio());
        }
        if (updateUserRequest.getEmail() != null) {
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getPhone() != null) {
            user.setPhone(updateUserRequest.getPhone());
        }
        userService.updateById(user);
        return Result.success(null);
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
        userService.updateById(user);
        return Result.success(null);
    }
    @DeleteMapping("/currentUser")
    public Result<Void> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        if(user == null) {
            return Result.error(ResultStatus.NOT_FOUND,"用户不存在");
        }
        boolean removed = userService.removeById(user.getId());
        if(!removed) {
            return Result.error(ResultStatus.NOT_FOUND);
        }
        return Result.success(null);
    }
}
