package todo.service.api.filter

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
 class CorsFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "*")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.addHeader("Access-Control-Allow-Headers", "*")

        if (request.method == "OPTIONS") {
            //response.setHeader("Access-Control-Max-Age", "1728000")
            response.status = HttpServletResponse.SC_OK
            response.writer.write(0)
        } else {
            filterChain.doFilter(request, response)
        }
    }
}