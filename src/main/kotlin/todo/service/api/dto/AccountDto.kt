package todo.service.api.dto

import todo.service.api.domain.model.Account
import todo.service.api.domain.model.type.Role


data class AccountDto(
    val accountId: Long,
    val email: String,
    val nickname: String,
    val role: Role,
    ) {
    constructor(account: Account) : this(
        accountId = account.accountId,
        email = account.email,
        nickname = account.nickname,
        role = account.role,
        )

        data class Create(
            val email: String,
            val password: String,
            val nickname: String,
        )


        data class Update(
            val password: String,
            val nickname: String,
            val role: Role,
        )

        data class UpdatePassword(
            val currentPw: String,
            val newPw: String
        )

    }
