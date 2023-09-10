package todo.service.api.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import todo.service.api.filter.CorsFilter
import todo.service.api.filter.JwtFilter
import todo.service.api.security.InternalLogInterceptor
import todo.service.api.security.JwtArgumentResolver

@Configuration
class WebConfig(
    private val jwtArgumentResolver: JwtArgumentResolver,
    private val jwtFilter: JwtFilter,
    private val internalLogInterceptor: InternalLogInterceptor,
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(jwtArgumentResolver)
        super.addArgumentResolvers(resolvers)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(internalLogInterceptor)
        super.addInterceptors(registry)
    }

    @Bean
    fun tokenAuthenticationFilterRegistrationBean(): FilterRegistrationBean<JwtFilter>? {
        val filterFilterRegistrationBean = FilterRegistrationBean<JwtFilter>()
        filterFilterRegistrationBean.filter = jwtFilter
        filterFilterRegistrationBean.addUrlPatterns(
            "/accounts/*",
            "/todos/*"
        )
        filterFilterRegistrationBean.order = 0
        return filterFilterRegistrationBean
    }

    @Bean
    fun corsFilterBean(): FilterRegistrationBean<CorsFilter> {
        val registration = FilterRegistrationBean<CorsFilter>()
        registration.filter = CorsFilter()
        registration.addUrlPatterns(
            "*"
        )
        registration.order = 0
        return registration
    }
}