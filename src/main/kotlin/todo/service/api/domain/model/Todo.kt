package todo.service.api.domain.model

import org.hibernate.annotations.DynamicUpdate
import todo.service.api.domain.model.type.Role
import todo.service.api.domain.model.type.TodoStatus
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "todo")
@DynamicUpdate
class Todo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id", nullable = false, insertable = false, updatable = false)
    val todoId: Long = -1,

    @Column(name = "account_id", nullable = false)
    val accountId: Long,

    @Column(name = "content", nullable = false)
    val content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: TodoStatus,

    @Column(name = "todo_at", nullable = false)
    val todoAt: LocalDateTime
    ) {
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, insertable = false, updatable = false)
    var account: Account? = null

    companion object {
        fun create(accountId: Long, content: String, todoAt: LocalDateTime): Todo {
            if (accountId == null || content.isBlank()) {
                throw IllegalArgumentException("유효하지 않은 todo 정보입니다.")
            }

            return Todo(
                accountId = accountId,
                content = content,
                status = TodoStatus.TODO,
                todoAt = todoAt,
            )
        }
    }

    fun updateStatus(status: TodoStatus) {
        this.status = status
    }
}