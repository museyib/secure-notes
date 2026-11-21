package llc.devlab.app.audit;

import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.List;

@Component
public class AuditEventMapper {

    public AuditEventDto toDto(AuditEvent auditEvent) {
        return new AuditEventDto(
                auditEvent.getId(),
                auditEvent.getOccurredAt(),
                auditEvent.getActorId(),
                auditEvent.getAction(),
                auditEvent.getRiskLevel(),
                auditEvent.getTargetType(),
                auditEvent.getTargetId(),
                auditEvent.getIpAddress());
    }

    public AuditEvent toEntity(AuditEventRequest request) {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setOccurredAt(Instant.now());
        auditEvent.setActorId(request.actorId());
        auditEvent.setAction(request.action());
        auditEvent.setRiskLevel(request.riskLevel());
        auditEvent.setTargetType(request.targetType());
        auditEvent.setTargetId(request.targetId());
        try {
            auditEvent.setIpAddress(InetAddress.getByName(request.ipAddress()));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return auditEvent;
    }

    public byte[] toCsv(List<AuditEvent> auditEvent) {
        StringBuilder sb = new StringBuilder("Id,OccurredAt,ActorId,Action,RiskLevel,TargetType,TargetId,IpAddress\n");
        auditEvent.forEach(audit -> sb.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                audit.getId(),
                audit.getOccurredAt(),
                audit.getActorId(),
                audit.getAction(),
                audit.getRiskLevel(),
                audit.getTargetType(),
                audit.getTargetId(),
                audit.getIpAddress())));
        return sb.toString().getBytes();
    }
}
