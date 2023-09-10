package todo.service.api.web.exception

import todo.service.api.web.exception.*
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler() {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BizBaseException::class)
    protected fun handleBizException(be: BizBaseException): ResponseEntity<ErrorResponseBody> {
        log.warn("[EXCEPTION] BizBaseException ${be.code} ${be.message}")

        return ErrorResponse(be.status, be.code, be.message)
    }

}