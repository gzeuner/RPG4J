package de.zeus.hermes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "RESPONSE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36)
    private UUID id;

    @Column(name = "REQUEST_ID", length = 36, nullable = false)
    private UUID requestId;

    @Column(name = "CLIENT_ID", length = 64, nullable = false)
    private String clientId;

    @Column(name = "RESPONSE_JSON", length = 4096, nullable = false)
    private String responseJson;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;
}
