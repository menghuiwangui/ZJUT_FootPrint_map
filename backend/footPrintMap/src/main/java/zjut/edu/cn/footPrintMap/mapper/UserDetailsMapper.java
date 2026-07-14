package zjut.edu.cn.footPrintMap.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zjut.edu.cn.footPrintMap.entity.User;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor  //自动写一个包含所有属性的全参构造方法
@Getter
public class UserDetailsMapper implements UserDetails {
    private String id;
    private String username;
    private String password;
    private Integer role;  // 0-普通用户，1-管理员

    public static UserDetailsMapper build(User user) {
        return new UserDetailsMapper(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = (role != null && role == 1) ? "ADMIN": "USER";
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
