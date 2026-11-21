package llc.devlab.app.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NoteRevokeRequest(@NotBlank
                                      @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
                                      String sharedUserEmail) {
}
