package todo.service.api.dto

import todo.service.api.domain.model.Todo
import todo.service.api.domain.model.type.TodoStatus
import java.time.LocalDateTime

data class TodoDto(
    val todoId: Long,
    val accountId: Long,
    val nickname: String,
    val content: String,
    val status: TodoStatus,
    val todoAt: LocalDateTime,
    val createdAt: LocalDateTime,
) {
    constructor(todo: Todo) : this(
        todoId = todo.todoId,
        accountId = todo.accountId,
        nickname = todo.account!!.nickname,
        content = todo.content,
        status = todo.status,
        todoAt = todo.todoAt,
        createdAt = todo.createdAt
    )

    data class Create(
        val content: String,
        val todoAt: LocalDateTime,
    )

    data class Update(
        val status: TodoStatus
    )

}