package zjut.edu.cn.footPrintMap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("http://localhost:5173");
        config.addAllowedHeader("*");              // 允许所有请求头
        config.addAllowedMethod("*");              // 允许所有 HTTP 方法
        config.setAllowCredentials(true);          // 允许携带 Cookie / Authorization
        config.setMaxAge(3600L);                   // 预检请求缓存时间
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config);
        return new CorsFilter(source);
    }
}
