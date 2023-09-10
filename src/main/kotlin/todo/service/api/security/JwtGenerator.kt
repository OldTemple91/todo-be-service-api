package todo.service.api.security

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.jsonwebtoken.*
import todo.service.api.domain.model.type.Role
import todo.service.api.dto.TokenDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import todo.service.api.filter.JwtFilter
import todo.service.api.web.exception.ErrorResponseBody
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtGenerator(
    @Value("\${jwt.secret}")
    private val secretKey: String
) {
    private val encodedSecretKey: String = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    private val algorithm = SignatureAlgorithm.HS256

    companion object {
        private val log = LoggerFactory.getLogger(JwtGenerator::class.java)
        private const val AUTHORITIES_KEY = "role"
        private const val USER_ID = "user_id"
        const val USER_EMAIL = "user_email"
        private const val ACCESS_TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000L // 1 Day
        private const val REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L // 7 Day
    }

    fun generateToken(
        email: String,
        accountId: Long,
        authorities: Role
    ): TokenDto {
        val now = System.currentTimeMillis()

        //Access token 생성
        val accessToken = Jwts.builder()
            .claim(USER_EMAIL, email)
            .claim(USER_ID, accountId)
            .claim(AUTHORITIES_KEY, authorities)
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + ACCESS_TOKEN_EXPIRE_TIME))
            .signWith(algorithm, encodedSecretKey)
            .compact()

        //Refresh token 생성
        val refreshToken = Jwts.builder()
            .claim(USER_EMAIL, email)
            .claim(USER_ID, accountId)
            .claim(AUTHORITIES_KEY, authorities)
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + REFRESH_TOKEN_EXPIRE_TIME))
            .signWith(algorithm, encodedSecretKey)
            .compact()

        return TokenDto(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun getCurrentUser(token: String): JwtLoginAccount {
            val claims = Jwts.parser().setSigningKey(secretKey.toByteArray()).parseClaimsJws(token).body

            return JwtLoginAccount(
                accountId = claims[USER_ID, Number::class.java].toLong(),
                email = claims[USER_EMAIL, String::class.java],
                role = Role.valueOf(claims[AUTHORITIES_KEY, String::class.java])
            )
    }

    fun getJwtLoginAccount(request: HttpServletRequest): JwtLoginAccount {
        return request.getAttribute("Authorization") as JwtLoginAccount
    }

    @Throws(IOException::class)
    private fun writeTokenErrorResponse(response: HttpServletResponse, e: JwtException) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(getErrorResponse(e))
    }

    @Throws(JsonProcessingException::class)
    private fun getErrorResponse(ex: JwtException): String {
        val error = ErrorResponseBody(ex)
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        return mapper.writeValueAsString(error)
    }
}