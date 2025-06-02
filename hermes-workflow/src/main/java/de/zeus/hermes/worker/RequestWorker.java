package de.zeus.hermes.worker;

import de.zeus.hermes.entity.RequestEntity;
import de.zeus.hermes.service.FileCommandDispatcher;
import de.zeus.hermes.service.RequestResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestWorker {

    private final RequestResponseService service;
    private final FileCommandDispatcher dispatcher;

    @Scheduled(fixedDelay = 2000)
    public void processOpenRequests() {
        List<RequestEntity> openRequests = service.getPendingRequests();
        if (openRequests.isEmpty()) return;

        for (RequestEntity request : openRequests) {
            try {
                log.info("⚙Processing request {} for client {}", request.getId(), request.getClientId());

                String[] args = dispatcher.deserializeArgs(request.getArgsJson());
                String responseText = dispatcher.dispatch(request.getCommand(), args);

                service.saveResponseAndMarkDone(request, responseText);

                log.info("Done request {}, response: {}", request.getId(), responseText);
            } catch (Exception e) {
                log.error("Fehler bei der Bearbeitung des Requests: {}", request.getId(), e);
            }
        }
    }
}
