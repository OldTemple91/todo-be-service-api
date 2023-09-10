package todo.service.api.web.exception

import org.springframework.http.HttpStatus
import java.lang.RuntimeException

open class BizBaseException(
    val status: HttpStatus,
    val code: String,
    override val message: String
) : RuntimeException() {
    constructor(errorCode: ErrorCode) : this (
        status = HttpStatus.valueOf(errorCode.status),
        code = errorCode.code,
        message = errorCode.message
    )
}

class BadInputException: BizBaseException(ErrorCode.BAD_INPUT_ERROR)