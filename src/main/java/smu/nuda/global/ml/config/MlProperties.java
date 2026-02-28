package smu.nuda.global.ml.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "ml")
public class MlProperties {
    @NotBlank private String baseUrl;
    @Positive private int connectTimeout;
    @Positive private int readTimeout;
}
