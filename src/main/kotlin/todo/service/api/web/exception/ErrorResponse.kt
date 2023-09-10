package todo.service.api.web.exception

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class ErrorResponse(
    val status: HttpStatus,
    val code: String,
    val message: String?
) : ResponseEntity<ErrorResponseBody>(
    ErrorResponseBody(
        status = status.value(),
        code = code,
        message = message
    ), status
){
    constructor(errorCode: ErrorCode): this(
        status = HttpStatus.valueOf(errorCode.status),
        code = errorCode.code,
        message = errorCode.message
    )

    constructor(e: JwtException) : this(
        status = HttpStatus.valueOf(401),
        code = "JE000",
        message = e.message.toString()
    )
}

data class ErrorResponseBody(
    val status: Int,
    val code: String,
    val message: String?
) {
    constructor(errorCode: ErrorCode) : this(
        status = errorCode.status,
        code = errorCode.code,
        message = errorCode.message
    )

    constructor(e: JwtException) : this(
        status = 401,
        code = "JE000",
        message = e.message.toString()
    )
}