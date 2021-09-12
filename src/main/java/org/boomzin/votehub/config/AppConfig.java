package org.boomzin.votehub.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import lombok.extern.slf4j.Slf4j;
import org.boomzin.votehub.util.TimeInterceptor;
import org.h2.tools.Server;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.sql.SQLException;

@Configuration
@Slf4j
@EnableCaching
public class AppConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        log.info("Start H2 TCP server");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    //    https://stackoverflow.com/a/46947975/548473
    @Bean
    Module module() {
        return new Hibernate5Module();
    }

//    http://www.luv2code.com/2020/01/06/spring-rest-api-how-to-disable-http-delete-put-methods/
    @Configuration
    public class InterceptConfig implements WebMvcConfigurer {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new TimeInterceptor()).addPathPatterns("/api/votes/**");
        }
    }
}
