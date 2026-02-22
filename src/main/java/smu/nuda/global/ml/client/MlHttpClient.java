package smu.nuda.global.ml.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class MlHttpClient {

    private final WebClient mlWebClient;

    public <T> T post(String uriTemplate, Object body, Class<T> responseType, Object... uriVariables) {
        return mlWebClient.post()
                .uri(uriTemplate, uriVariables)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public <T> T get(String uri, Class<T> responseType) {
        return mlWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}
