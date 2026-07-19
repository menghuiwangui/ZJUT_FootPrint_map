package zjut.edu.cn.footPrintMap.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import zjut.edu.cn.footPrintMap.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //开启@PreAuthorize接口注解权限
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CorsConfig corsConfig;

    //密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //认证管理器
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        httpSecurity.sessionManagement(
                //禁用Session、CSRF
                s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                    .csrf(AbstractHttpConfigurer::disable)
                //放行登录注册接口，其余需鉴权
                    .authorizeHttpRequests(
                            auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                                    .permitAll()
                                    .requestMatchers(
                                    "/api/user/login",
                                            "/api/user/register",
                                            //http://localhost:8080/swagger-ui/index.html
                                            "/swagger-ui/**",
                                            "/v3/api-docs/**")
                                    .permitAll() //放行这些接口
                                    //其他接口都要登录
                                    .anyRequest()
                                    .authenticated())
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // 1. 显式关闭 CSRF，这是解决 403 的核心
//                .csrf(csrf -> csrf.disable())
//
//                // 2. 显式放行登录接口，不要用 permitAll() 的默认行为
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/user/login").permitAll() // 放行登录
//                        .requestMatchers("/**").permitAll()             // 开发阶段甚至可以放行所有
//                        .anyRequest().authenticated()
//                )
//
//                // 3. 禁用 Session 验证 (可选，但推荐在前后端分离项目加上)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }

}
