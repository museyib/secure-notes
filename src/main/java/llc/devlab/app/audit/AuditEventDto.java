package llc.devlab.app.audit;

import java.net.InetAddress;
import java.time.Instant;

public record AuditEventDto(Long id,
                            Instant occurredAt,
                            String actorId,
                            AuditAction action,
                            RiskLevel riskLevel,
                            String targetType,
                            String targetId,
                            InetAddress ipAddress) {
}
