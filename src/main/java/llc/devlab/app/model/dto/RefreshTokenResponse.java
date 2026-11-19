package llc.devlab.app.model.dto;

public record RefreshTokenResponse(String accessToken, long expiresIn) {
}
