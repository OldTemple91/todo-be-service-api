package todo.service.api.security

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import todo.service.api.filter.JwtFilter
import todo.service.api.web.exception.ResourceNotFoundException
import java.util.*

@Component
class JwtArgumentResolver(
    private val jwtGenerator: JwtGenerator
) : HandlerMethodArgumentResolver {

    companion object {
        fun getCurrentUserJWT(webRequest: NativeWebRequest): Optional<String> {
            val bearerToken = webRequest.getHeader(JwtFilter.AUTHORIZATION_HEADER)
            return if (StringUtils.hasText(bearerToken) && bearerToken!!.startsWith(JwtFilter.BEARER_PREFIX)) {
                Optional.of(bearerToken.substring(7))
            } else Optional.empty()
        }
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.getParameterAnnotation(JwtAccount::class.java) != null
    }

    @Throws(Exception::class)
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): JwtLoginAccount {
        val token = getCurrentUserJWT(webRequest).orElseThrow { ResourceNotFoundException() }

        return jwtGenerator.getCurrentUser(token)
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class JwtAccount
