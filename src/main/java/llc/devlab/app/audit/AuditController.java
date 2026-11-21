package llc.devlab.app.audit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    @GetMapping
    public List<AuditEventDto> getEvents(@Valid @ModelAttribute AuditFilterRequest filter) {
        return auditService.getEvents(filter);
    }

    @GetMapping("/export")
    public byte[] exportToCsv(@Valid @ModelAttribute AuditFilterRequest filter) {
        return auditService.exportToCsv(filter);
    }
}
