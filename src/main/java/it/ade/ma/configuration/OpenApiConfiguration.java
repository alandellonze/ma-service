package it.ade.ma.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${spring.application.name}") String name,
            @Value("${spring.application.version}") String version,
            @Value("${spring.application.description}") String description) {
        return new OpenAPI()
                .info(new Info()
                        .title(name + " API")
                        .version(version)
                        .description(description));
    }

}
