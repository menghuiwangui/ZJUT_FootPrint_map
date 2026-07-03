package zjut.edu.cn.FootPrintMap.config;//package zjut.edu.cn.footPrintMap.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo() {
//        Contact contact = new Contact("menghuiwangui && superideol",
//                "https://www.baidu.com",
//                "2964668101@qq.com");
//        return new ApiInfoBuilder()
//                .title("足迹记录项目API")
//                .description("两人协作的旅行足迹记录系统项目SwaggerAPI管理")
//                .termsOfServiceUrl("https://www.baidu.com")
//                .version("1.0")
//                .contact(contact)
//                .build();
//    }
//}
//
////http://localhost:8080/swagger-ui.html
////后续扩展成接口组
//依赖
//<!-- ❌ 删掉这两个 -->
//<dependency>
//    <groupId>io.springfox</groupId>
//    <artifactId>springfox-swagger2</artifactId>
//    <version>2.9.2</version>
//</dependency>
//<dependency>
//    <groupId>io.springfox</groupId>
//    <artifactId>springfox-swagger-ui</artifactId>
//    <version>2.9.2</version>
//</dependency>