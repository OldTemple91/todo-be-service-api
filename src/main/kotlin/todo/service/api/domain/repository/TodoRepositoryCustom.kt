package todo.service.api.domain.repository

import todo.service.api.domain.model.Todo

interface TodoRepositoryCustom {
    fun getTodoListByUserId(userId: Long): List<Todo>

    fun getRecentTodoByUserId(userId: Long): Todo?
}