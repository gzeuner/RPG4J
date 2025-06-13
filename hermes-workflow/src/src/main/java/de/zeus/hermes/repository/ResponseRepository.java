package de.zeus.hermes.repository;

import de.zeus.hermes.entity.ResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResponseRepository extends JpaRepository<ResponseEntity, UUID> {

    List<ResponseEntity> findByClientId(String clientId);

    Optional<ResponseEntity> findTopByRequestIdOrderByCreatedAtDesc(UUID requestId);

    Optional<ResponseEntity> findTopByClientIdOrderByCreatedAtDesc(String clientId);
}
