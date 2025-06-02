package de.zeus.hermes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "REQUEST")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36)
    private UUID id;

    @Column(name = "CLIENT_ID", length = 64, nullable = false)
    private String clientId;

    @Column(name = "COMMAND", length = 64, nullable = false)
    private String command;

    @Column(name = "ARGS_JSON", length = 4096, nullable = false)
    private String argsJson;

    @Column(name = "STATUS", length = 16, nullable = false)
    private String status;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) status = "PENDING";
    }
}
