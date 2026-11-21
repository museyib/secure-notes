package llc.devlab.app.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;
    private final AuditEventMapper auditEventMapper;


    public List<AuditEventDto> getEvents(AuditFilterRequest filter) {
        return auditRepository.findAll(AuditSpecification.fromFilter(filter)).stream()
                .map(auditEventMapper::toDto)
                .toList();
    }

    public byte[] exportToCsv(AuditFilterRequest filter) {
        return auditEventMapper.toCsv(auditRepository.findAll(AuditSpecification.fromFilter(filter)));
    }

    @Transactional
    public void logEvent(AuditEventRequest request) {
        AuditEvent auditEvent = auditEventMapper.toEntity(request);
        auditRepository.save(auditEvent);
    }
}
