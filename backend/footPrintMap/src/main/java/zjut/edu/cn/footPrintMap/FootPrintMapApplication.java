package zjut.edu.cn.footPrintMap;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("zjut.edu.cn.footPrintMap.mapper")
public class FootPrintMapApplication {
	public static void main(String[] args) {
		SpringApplication.run(FootPrintMapApplication.class, args);
	}

}
