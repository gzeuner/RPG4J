package de.zeus.hermes.service;

import de.zeus.hermes.entity.RequestEntity;
import de.zeus.hermes.entity.ResponseEntity;
import de.zeus.hermes.repository.RequestRepository;
import de.zeus.hermes.repository.ResponseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestResponseService {

    private final RequestRepository requestRepository;
    private final ResponseRepository responseRepository;

    @Transactional
    public RequestEntity createRequest(String clientId, String command, String argsJson) {
        String id = UUID.randomUUID().toString();
        RequestEntity request = RequestEntity.builder()
                //.id(id)
                .clientId(clientId)
                .command(command)
                .argsJson(argsJson)
                .status("PENDING")
                .build();
        log.info("📨 Neuer Request gespeichert: {}", request);
        return requestRepository.save(request);
    }

    public void updateRequestStatus(UUID requestId, String newStatus) {
        requestRepository.findById(requestId).ifPresent(request -> {
            request.setStatus(newStatus);
            requestRepository.save(request);
            log.info("✅ Request {} Status aktualisiert auf {}", requestId, newStatus);
        });
    }

    public List<RequestEntity> getPendingRequests() {
        return requestRepository.findByStatus("PENDING");
    }

    public void createResponse(UUID requestId, String clientId, String responseJson) {
        String id = UUID.randomUUID().toString();
        ResponseEntity response = ResponseEntity.builder()
               // .id(id)
                .requestId(requestId)
                .clientId(clientId)
                .responseJson(responseJson)
                .build();
        responseRepository.save(response);
        log.info("📬 Neue Response gespeichert zu Request {}", requestId);
    }

    public Optional<ResponseEntity> getLatestResponseForClient(String clientId) {
        return responseRepository.findTopByClientIdOrderByCreatedAtDesc(clientId);
    }

    public Optional<ResponseEntity> getResponseForRequest(String requestId) {
        return null;
        //return responseRepository.findTopByRequestIdOrderByCreatedAtDesc(requestId);
    }

    @Transactional
    public void saveResponse(ResponseEntity response) {
        responseRepository.save(response);
        log.info("💾 Response gespeichert: {}", response.getId());
    }

    @Transactional
    public void markRequestAsDone(RequestEntity request) {
        request.setStatus("DONE");
        requestRepository.save(request);
        log.info("🟢 Request als DONE markiert: {}", request.getId());
    }

    @Transactional
    public void saveResponseAndMarkDone(RequestEntity request, String responseJson) {
        String responseId = UUID.randomUUID().toString();
        ResponseEntity response = ResponseEntity.builder()
              //  .id(responseId)
                .requestId(request.getId())
                .clientId(request.getClientId())
                .responseJson(responseJson)
                .build();

        responseRepository.save(response);

        request.setStatus("DONE");
        requestRepository.save(request);

        log.info("📝 Request {} abgeschlossen und Response gespeichert", request.getId());
    }
}
