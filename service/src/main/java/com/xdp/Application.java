package com.xdp;

import com.xdp.lib.annotation.EnableStringTrim;
import com.xdp.lib.annotation.GlobalException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"vn.ngs.nspace", "com.xdp"})
@EnableScheduling
@EnableStringTrim
@GlobalException
@EnableAsync
@EnableJpaRepositories({"vn.ngs.nspace", "com.xdp"})
@EntityScan(basePackages = {"vn.ngs.nspace", "com.xdp"})
@OpenAPIDefinition(info = @Info(title = "HCM SD Service", version = "0.1", description = "Staff Planning Information"))
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
