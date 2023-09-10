package todo.service.api.web.controller

import org.springframework.web.bind.annotation.*
import todo.service.api.domain.service.TodoService
import todo.service.api.dto.TodoDto
import todo.service.api.security.JwtAccount
import todo.service.api.security.JwtLoginAccount

@RestController
@RequestMapping("/todos")
class TodoController(
    private val todoService: TodoService
) {
    @PostMapping
    fun createTodo(
        @JwtAccount account: JwtLoginAccount,
        @RequestBody create: TodoDto.Create
    ) {
        todoService.createTodo(account, create)
    }

    @GetMapping
    fun getTodoListByUserId(
        @JwtAccount account: JwtLoginAccount,
    ): List<TodoDto> {
        return todoService.getTodoListByUserId(account)
    }

    @GetMapping("/recent")
    fun getRecentTodoByUserId(
        @JwtAccount account: JwtLoginAccount,
    ): TodoDto {
        return todoService.getRecentTodoByUserId(account)
    }

    @PutMapping("/{id}/status")
    fun updateTodoStatus(
        @PathVariable(name = "id") id: Long,
        @RequestBody status: TodoDto.Update,
        @JwtAccount account: JwtLoginAccount
    ) {
        todoService.updateTodoStatus(account, id, status)
    }

}