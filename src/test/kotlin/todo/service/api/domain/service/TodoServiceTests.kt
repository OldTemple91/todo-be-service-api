package todo.service.api.domain.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import todo.service.api.domain.repository.AccountRepository
import todo.service.api.domain.repository.TodoRepository
import todo.service.api.dto.AccountDto
import todo.service.api.dto.TodoDto
import todo.service.api.security.JwtLoginAccount
import java.time.LocalDateTime


@SpringBootTest
@Transactional
class TodoServiceTests(
    @Autowired private val todoRepository: TodoRepository,
    @Autowired private val accountRepository: AccountRepository,
    @Autowired private val accountService: AccountService,
    @Autowired private val todoService: TodoService,
) {

    @Test
    @DisplayName("Todo 생성 테스트")
    fun create_correctly_saves_todo() {
        // Arrange
        val createAccount = AccountDto.Create(
            email = "test@email.com",
            password = "1234",
            nickname = "testName"
        )
        accountService.createAccount(createAccount)
        val account = accountRepository.findAll()[0]

        val jwtInfoByUser = JwtLoginAccount(
            accountId = account.accountId,
            email = account.email,
            role = account.role
        )

        val todo = TodoDto.Create(
            content = "Todo 메모",
            todoAt = LocalDateTime.of(2023, 9, 10, 20, 0)
        )

        // Act
        todoService.createTodo(jwtInfoByUser, todo)

        // Assert
        assertThat(todoRepository.findAll()).hasSize(1)
        val actual = todoRepository.findAll()[0]
        assertThat(actual.accountId).isEqualTo(account.accountId)
        assertThat(actual.content).isEqualTo("Todo 메모")
        assertThat(actual.todoAt).isEqualTo(LocalDateTime.of(2023, 9, 10, 20, 0))
    }

    @Test
    @DisplayName("가장 최근의 Todo 조회 테스트")
    fun getRecentTodo_correctly_return() {
        // Arrange
        val createAccount = AccountDto.Create(
            email = "test@email.com",
            password = "1234",
            nickname = "testName"
        )
        accountService.createAccount(createAccount)
        val account = accountRepository.findAll()[0]

        val jwtInfoByUser = JwtLoginAccount(
            accountId = account.accountId,
            email = account.email,
            role = account.role
        )

        val testTodo1 = TodoDto.Create(
            content = "Todo 1시간 전 과거 내용",
            todoAt = LocalDateTime.now().minusHours(1)
        )
        todoService.createTodo(jwtInfoByUser, testTodo1)

        val testTodo2 = TodoDto.Create(
            content = "Todo 1시간 후 미래 내용",
            todoAt = LocalDateTime.now().plusHours(1)
        )
        todoService.createTodo(jwtInfoByUser, testTodo2)

        // Act
        val actual = todoRepository.getRecentTodoByUserId(account.accountId)

        // Assert
        val todo1 = todoRepository.findAll()[0]
        val todo2 = todoRepository.findAll()[1]
        assertThat(todoRepository.findAll()).hasSize(2)
        assertThat(todo1.accountId).isEqualTo(account.accountId)
        assertThat(todo1.content).isEqualTo("Todo 1시간 전 과거 내용")
        assertThat(todo2.accountId).isEqualTo(account.accountId)
        assertThat(todo2.content).isEqualTo("Todo 1시간 후 미래 내용")

        assertThat(actual).isNotNull
        assertThat(actual!!.todoId).isEqualTo(todo2.todoId)
        assertThat(actual!!.todoAt).isEqualTo(todo2.todoAt)
    }
}