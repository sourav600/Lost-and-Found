package com.moinul.LostAndFound.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import io.swagger.v3.oas.models.media.Schema;

@Configuration
public class SwaggerConfiguration {
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${apiTitle}") String apiTitle,
            @Value("${apiDescription}") String apiDescription,
            @Value("${apiVersion}") String apiVersion,
            @Value("${apiContactName}") String apiContactName,
            @Value("${apiContactEmail}") String apiContactEmail,
            @Value("${apiContactUrl}") String apiContactUrl

    ) {
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement().addList("bearer")
                ).components(
                        new Components()
                                .addSecuritySchemes("bearer", createAPIKeyScheme())
//                                .addSchemas("MultipartFile", new Schema().type("string").format("binary"))


                ).info(new Info()
                        .title(apiTitle)
                        .description(apiDescription)
                        .version(apiVersion)
                        .contact(new Contact().name(apiContactName).email(apiContactEmail).url(apiContactUrl))
                );
    }

}

