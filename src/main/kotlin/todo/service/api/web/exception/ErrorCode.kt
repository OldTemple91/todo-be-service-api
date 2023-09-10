package todo.service.api.web.exception

enum class ErrorCode(
    val status: Int,
    val code: String,
    val message: String,
) {
    // common error code
    BAD_INPUT_ERROR(400, "C0003", "Invalid Bad Input"),
    RESOURCE_NOT_FOUND(404, "C0004", "Resource Not Found"),
    INTERNAL_SERVER_ERROR(500, "C9999", "Internal Server Error"),

    // account
    PASSWORD_NOT_MATCH(400, "AC0000", "비밀번호가 일치하지 않습니다."),
    EMAIL_NOT_FOUND(404, "AC0001", "이메일을 찾을 수 없습니다."),
    WITHDRAW_ACCOUNT(403, "AC0002", "탈퇴한 유저 입니다."),
    DUPLICATE_ACCOUNT(403, "AC0003", "이미 존재하는 이메일입니다."),
    ACCOUNT_NOT_FOUND(404, "AC0004", "유저를 찾을 수 없습니다."),

    // to do
    TODO_NOT_FOUND(404, "TD0000", "투두를 찾을 수 없습니다."),
    TODO_OWNER_NOT_MATCH(400, "TD0001", "투두 작성자와 일치하지 않습니다."),

}

