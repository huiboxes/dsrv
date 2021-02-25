package life.aftert.databox.mybatis.test;

import life.aftert.databox.mybatis.DxDataSourceConfig;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Import(DxDataSourceConfig.class)
@PropertySource("classpath:application.properties")
@ComponentScan("life.aftert.databox.*")
@MapperScan("life.aftert.databox.*")
public class BaseTest {
}
