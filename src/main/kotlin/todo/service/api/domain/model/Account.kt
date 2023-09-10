package todo.service.api.domain.model

import todo.service.api.domain.model.type.Role
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.*

@Entity
@Table(name = "account")
@DynamicUpdate
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", nullable = false, insertable = false, updatable = false)
    val accountId: Long = -1,

    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @Column(name = "password", nullable = false)
    val password: String,

    @Column(name = "nickname", nullable = false)
    val nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    val role: Role,

    @Column(name = "withdraw")
    var withdrawYn: Boolean,

    ){

companion object {
    fun create(email: String, password: String, nickname: String): Account {
        if (email.isBlank() || password.isBlank() || nickname.isBlank()) {
            throw IllegalArgumentException("유효하지 않은 사용자 정보입니다.")
        }

        return Account(
            email = email,
            password = password,
            nickname = nickname,
            role = Role.USER,
            withdrawYn = false
        )
    }
}

    fun withdraw() {
        this.withdrawYn = true
    }
}