package llc.devlab.app.audit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CurrentActorProvider {

    public String getCurrentActorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal() instanceof String) {
            return "ANONYMOUS";
        }

        if (authentication.getPrincipal() instanceof UserDetails principal) {
            return principal.getUsername();
        }

        return null;
    }
}
