package smu.nuda.global.ml.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ml")
public class MlProperties {
    private String baseUrl;
    private int connectTimeout;
    private int readTimeout;
}
