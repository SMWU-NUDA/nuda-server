package smu.nuda.global.ml.config;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Slf4j
@Configuration
@EnableConfigurationProperties(MlProperties.class)
@RequiredArgsConstructor
public class MlClientConfig {

    private final MlProperties mlProperties;

    @Bean
    public WebClient mlWebClient() {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, mlProperties.getConnectTimeout())
                .responseTimeout(Duration.ofMillis(mlProperties.getReadTimeout()));

        return WebClient.builder()
                .baseUrl(mlProperties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(loggingFilter())
                .build();
    }

    private ExchangeFilterFunction loggingFilter() {
        return (request, next) -> {
            log.debug("[ML] {} {}", request.method(), request.url());
            return next.exchange(request)
                    .doOnNext(response ->
                            log.debug("[ML] Response Status: {}", response.statusCode())
                    );
        };
    }
}
