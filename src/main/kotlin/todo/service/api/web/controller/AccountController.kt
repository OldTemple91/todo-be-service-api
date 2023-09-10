package todo.service.api.web.controller

import todo.service.api.domain.service.AccountService
import todo.service.api.dto.AccountDto
import org.springframework.web.bind.annotation.*
import todo.service.api.security.JwtAccount
import todo.service.api.security.JwtLoginAccount

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountService: AccountService
) {
    @PostMapping
    fun createAccount(@RequestBody create: AccountDto.Create) {
        accountService.createAccount(create)
    }

    @PutMapping("/{id}/withdraw")
    fun withdrawAccount(
        @PathVariable id: Long,
        @JwtAccount account: JwtLoginAccount
    ) {
        accountService.withdrawAccount(id, account)
    }

}