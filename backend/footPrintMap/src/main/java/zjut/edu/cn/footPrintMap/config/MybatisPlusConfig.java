package zjut.edu.cn.FootPrintMap.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan("zjut.edu.cn.FootPrintMap.mapper")
public class MybatisPlusConfig {

    @Bean
    public org.apache.ibatis.session.SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 如果有自定义 XML mapper 文件，取消下面注释并配置路径
        // factoryBean.setMapperLocations(
        //     new PathMatchingResourcePatternResolver()
        //         .getResources("classpath*:mapper/**/*.xml")
        // );

        return factoryBean.getObject();
    }
}