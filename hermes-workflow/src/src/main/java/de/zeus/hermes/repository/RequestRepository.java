package de.zeus.hermes.repository;

import de.zeus.hermes.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, UUID> {

    List<RequestEntity> findByClientId(String clientId);

    List<RequestEntity> findByStatus(String status);

    List<RequestEntity> findByStatusAndClientId(String status, String clientId);
}
