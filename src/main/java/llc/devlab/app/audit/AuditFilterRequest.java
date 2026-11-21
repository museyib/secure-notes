package llc.devlab.app.audit;

import java.time.Instant;

public record AuditFilterRequest(String actorId, String action, String targetType, String targetId, Instant dateFrom, Instant dateTo) {
}
