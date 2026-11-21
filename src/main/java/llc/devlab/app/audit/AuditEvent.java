package llc.devlab.app.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "audit_event")
public class AuditEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Instant occurredAt;

    private String actorId;

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    private String targetType;

    private String targetId;

    private InetAddress ipAddress;

    private Instant createdAt = Instant.now();
}