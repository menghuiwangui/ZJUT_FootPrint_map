package zjut.edu.cn.footPrintMap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import zjut.edu.cn.footPrintMap.myConst.MyPathConst;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        resourceHandlerRegistry.addResourceHandler("/Uploads/avatar/**")
                .addResourceLocations("file:"+ MyPathConst.getAvatarPath()+"/");
        resourceHandlerRegistry.addResourceHandler("/uploads/travelImage/**")
                .addResourceLocations("file:" + MyPathConst.getTravelImagePath() + "/");
    }
}
