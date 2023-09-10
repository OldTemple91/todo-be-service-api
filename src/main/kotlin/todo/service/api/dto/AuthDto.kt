package todo.service.api.dto

data class LoginDto(
    val email: String,
    val password: String
)

data class TokenDto(
    val accessToken: String,
    val refreshToken: String
)

data class RefreshTokenDto(
    val refreshToken: String
)