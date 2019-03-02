package org.trancemountain.storageservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

/**
 * The Swagger 2 configuration.  Enabled only for running web applications.
 */
@Configuration
@EnableSwagger2WebFlux
@ConditionalOnWebApplication
public class SwaggerConfiguration {

    @Value("${spring.application.name:}")
    private String appName;

    @Bean
    public Docket standardApiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Standard Controllers")
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.trancemountain.storageservice.controller"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(appName).build();
    }

}
