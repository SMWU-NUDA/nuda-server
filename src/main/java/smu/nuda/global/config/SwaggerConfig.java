package smu.nuda.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "NUDA API",
                description = "NUDA 서비스 백엔드 API 문서",
                version = "v1"
        )
)
@io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = "JWT",
        description = " AccessToken 또는 Signup Token만 입력해주세요.",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

        @Bean
        public OpenAPI openAPI() {
                final String jwtSchemeName = "JWT";

                return new OpenAPI()
                        .addSecurityItem(new SecurityRequirement().addList(jwtSchemeName))
                        .components(
                                new io.swagger.v3.oas.models.Components()
                                        .addSecuritySchemes(
                                                jwtSchemeName,
                                                new SecurityScheme()
                                                        .type(SecurityScheme.Type.HTTP)
                                                        .scheme("bearer")
                                                        .bearerFormat("JWT")
                                        )
                        );
        }
}
