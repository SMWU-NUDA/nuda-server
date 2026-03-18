package smu.nuda.domain.search.sync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchSyncRunner {

    private final ProductSearchSyncExecutor productSearchSyncExecutor;

    @Async("eventExecutor")
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        productSearchSyncExecutor.sync();
    }
}