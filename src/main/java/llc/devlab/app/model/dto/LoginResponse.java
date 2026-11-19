package llc.devlab.app.model.dto;

public record LoginResponse(String username, String accessToken, Long expiresIn, String refreshToken) {
}
