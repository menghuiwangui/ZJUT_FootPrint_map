package zjut.edu.cn.footPrintMap.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import zjut.edu.cn.footPrintMap.common.utils.JWTUtil;
import zjut.edu.cn.footPrintMap.mapper.UserDetailsMapper;
import zjut.edu.cn.footPrintMap.service.impl.UserDetailsServiceImpl;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
        String path = request.getRequestURI();

        // 👇 CORS 预检请求直接放行，不做任何处理
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // 如果是登录、注册等公开接口，直接放行，不解析 Token
        if (path.startsWith("/api/user/login") ||
                path.startsWith("/api/user/register") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs")) {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        }

        String header = request.getHeader("Authorization");
        //取token
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String user_id = jwtUtil.getUserId(header.substring(7));
                String username = jwtUtil.getUsername(header.substring(7));
                //无上下文认证信息时，手动装入认证
                if (jwtUtil.verifyToken(header.substring(7))) {
                    if (user_id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetailsMapper userDetailsMapper = (UserDetailsMapper) userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetailsMapper, null, userDetailsMapper.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }catch (Exception e){
                logger.error("JWT authentication failed: " + e.getMessage());
            }
        }
        try {
            filterChain.doFilter(request,response);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

}
