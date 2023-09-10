package todo.service.api.security

import todo.service.api.domain.model.type.Role

data class JwtLoginAccount(
    val accountId: Long,
    val email: String,
    val role: Role
) {
    fun isAdmin(): Boolean {
        return this.role == Role.ADMIN
    }
}

