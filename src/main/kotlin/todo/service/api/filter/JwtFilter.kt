package todo.service.api.filter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.jsonwebtoken.*
import todo.service.api.web.exception.ErrorResponseBody
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import todo.service.api.security.JwtGenerator
import todo.service.api.security.JwtLoginAccount
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Order(1)
class JwtFilter(
    private val jwtGenerator: JwtGenerator,
) : OncePerRequestFilter() {

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        private val log = LoggerFactory.getLogger(JwtFilter::class.java)
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt = resolveToken(request)

        if(jwt.isNullOrEmpty()) {
            log.info("JWT Token is Null")
            val e = UnsupportedJwtException("JWT Token is Null")
            writeTokenErrorResponse(response, e)
            return
        }

        try {
            val loginAccount = jwtGenerator.getCurrentUser(jwt)
            setLoginRider(request, loginAccount)
        } catch (e: MalformedJwtException) {
            log.info("잘못된 JWT 서명입니다.")
            writeTokenErrorResponse(response, e)
            return
        } catch (e: ExpiredJwtException) {
            log.info("만료된 JWT 토큰입니다.")
            writeTokenErrorResponse(response, e)
            return
        } catch (e: UnsupportedJwtException) {
            log.info("지원되지 않는 JWT 토큰입니다.")
            writeTokenErrorResponse(response, e)
            return
        } catch (e: IllegalArgumentException) {
            log.info("JWT 토큰이 잘못되었습니다.")
            //writeTokenErrorResponse(response, e)
            return
        } catch (e: SignatureException) {
            log.info("JWT Signature Exception")
            writeTokenErrorResponse(response, e)
            return
        }

        filterChain.doFilter(request, response)
    }

    fun setLoginRider(request: HttpServletRequest, loginAccount: JwtLoginAccount) {
        request.setAttribute(AUTHORIZATION_HEADER, loginAccount)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(7)
        } else null
    }

    @Throws(ServletException::class)
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        val httpMethod = request.method
        return (httpMethod == "POST" && path == "/accounts")
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