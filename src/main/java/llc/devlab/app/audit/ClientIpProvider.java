package llc.devlab.app.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientIpProvider {
    private final HttpServletRequest request;

    public String getClientIp() {
        return request.getRemoteAddr();
    }
}
