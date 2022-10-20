package com.abn.amro.recipe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
        // use setStatusCode(HttpStatus.XYZ) for any custom status code if required, e.g. MOVED_PERMANENTLY
        registry.addRedirectViewController("/swagger-ui", "/swagger-ui/index.html");
        // any other alias
    }


    private ApiInfo apiInfo() {
        return new ApiInfo("Recipe Rest APIs",
                "APIs for Recipe api.",
                "1.0",
                "https://terms.abn.com",
                new Contact("ABN", "www.abn.amro.com", "info@abn.amro.com"),
                "Apache 2.0",
                "https://licence.abn.com",
                Collections.emptyList());
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}