package todo.service.api.web.controller

import todo.service.api.domain.exception.PasswordNotMatchException
import todo.service.api.domain.exception.WithdrawAccountException
import todo.service.api.domain.service.AccountService
import todo.service.api.dto.LoginDto
import todo.service.api.dto.RefreshTokenDto
import todo.service.api.dto.TokenDto
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import todo.service.api.security.JwtGenerator
import javax.validation.Valid

@RestController
@RequestMapping
class AuthController(
    private val accountService: AccountService,
    private val jwtGenerator: JwtGenerator,
) {
    @PostMapping("/authenticate")
    fun login(
        @Valid @RequestBody loginDto: LoginDto,
    ): TokenDto {
        val account = accountService.getAccountByEmail(loginDto.email)
        if(account.password != loginDto.password) {
            throw PasswordNotMatchException()
        }
        if(account.withdrawYn) {
            throw WithdrawAccountException()
        }

        return jwtGenerator.generateToken(account.email, account.accountId, account.role)
    }

    @PostMapping("/reissue")
    fun updateToken(
        @RequestBody refreshTokenDto: RefreshTokenDto
    ): TokenDto {
        val token = jwtGenerator.getCurrentUser(refreshTokenDto.refreshToken)
        val account = accountService.getAccountById(token.accountId)

        return jwtGenerator.generateToken(account.email, account.accountId, account.role)
    }
}