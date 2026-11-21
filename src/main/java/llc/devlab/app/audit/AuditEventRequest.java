package llc.devlab.app.audit;

public record AuditEventRequest(String actorId, AuditAction action, RiskLevel riskLevel, String targetType, String targetId, String ipAddress) {
}
