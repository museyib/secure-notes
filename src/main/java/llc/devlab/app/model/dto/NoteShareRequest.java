package llc.devlab.app.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import llc.devlab.app.security.model.enums.ShareMode;

public record NoteShareRequest(@NotBlank
                               @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
                               String sharedUserEmail,
                               @NotNull ShareMode shareMode) {
}
