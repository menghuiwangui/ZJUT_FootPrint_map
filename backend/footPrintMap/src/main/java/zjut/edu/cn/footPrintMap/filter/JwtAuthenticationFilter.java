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

        String header = request.getHeader("Authorization");
        //取token
        if(header != null && header.startsWith("Bearer ")) {
            String user_id = jwtUtil.getUserId(header.substring(7));
            String username = jwtUtil.getUsername(header.substring(7));
            //无上下文认证信息时，手动装入认证
            if(jwtUtil.verifyToken(header.substring(7))) {
                if(user_id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetailsMapper userDetailsMapper = (UserDetailsMapper) userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetailsMapper,null,userDetailsMapper.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        try {
            filterChain.doFilter(request,response);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

}
