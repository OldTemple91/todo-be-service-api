package todo.service.api.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import todo.service.api.domain.exception.TodoNotFoundException
import todo.service.api.domain.exception.TodoOwnerNotMatchException
import todo.service.api.domain.model.Todo
import todo.service.api.domain.repository.TodoRepository
import todo.service.api.dto.TodoDto
import todo.service.api.security.JwtLoginAccount

@Service
@Transactional
class TodoService(
    private val todoRepository: TodoRepository,
    private val accountService: AccountService,
) {
    fun createTodo(account: JwtLoginAccount, create: TodoDto.Create) {
        val todo = Todo.create(
            accountId = account.accountId,
            content = create.content,
            todoAt = create.todoAt
            )
        todoRepository.save(todo)
    }

    fun getTodoListByUserId(account: JwtLoginAccount): List<TodoDto> {
        val todos = todoRepository.getTodoListByUserId(account.accountId)

        return todos.map { TodoDto(it) }
    }

    fun getRecentTodoByUserId(account: JwtLoginAccount): TodoDto {
        val todoOrNull = todoRepository.getRecentTodoByUserId(account.accountId) ?: throw TodoNotFoundException()

        return TodoDto(todoOrNull)
    }

    fun updateTodoStatus(account: JwtLoginAccount, todoId: Long, update: TodoDto.Update) {
        val user = accountService.getAccountById(account.accountId)
        val todo = todoRepository.findById(todoId).orElseThrow { TodoNotFoundException() }
        if(user.accountId != todo.accountId) {
            throw TodoOwnerNotMatchException()
        }
        todo.updateStatus(update.status)

        todoRepository.save(todo)
    }

}