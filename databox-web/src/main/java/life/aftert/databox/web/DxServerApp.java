package life.aftert.databox.web;

import life.aftert.databox.mybatis.DxDataSourceConfig;
import life.aftert.databox.web.security.SecurityInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.*;


@EnableWebMvc
@SuppressWarnings("deprecation")
@Configuration
@ComponentScan({"life.aftert.databox.*"})
@SpringBootApplication
@Import({DxDataSourceConfig.class, DxServerBeanConfiguration.class})
@MapperScan("life.aftert.databox")
public class DxServerApp {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private SecurityInterceptor securityInterceptor;

    public static void main(String[] args) {
        SpringApplication.run(DxServerApp.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer(){
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedHeaders("*")
//                        .allowedOrigins("*")
//                        .allowedMethods("GET", "POST", "DELETE", "PUT","OPTIONS","PATCH")
//                        .allowCredentials(true)
//                        .maxAge(3600);
//            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(securityInterceptor);
            }
        };
    }

    @Bean
    public CookieSerializer httpSessionIdResolver() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setUseHttpOnlyCookie(false);
        cookieSerializer.setSameSite(null);
        return cookieSerializer;
    }

}
