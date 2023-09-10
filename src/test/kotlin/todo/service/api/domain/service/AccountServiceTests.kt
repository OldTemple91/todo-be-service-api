package todo.service.api.domain.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import todo.service.api.domain.repository.AccountRepository
import todo.service.api.dto.AccountDto
import todo.service.api.security.JwtLoginAccount


@SpringBootTest
@Transactional
class AccountServiceTests(
    @Autowired private val accountRepository: AccountRepository,
    @Autowired private val accountService: AccountService,
) {

    @Test
    @DisplayName("유저 생성 테스트")
    fun join_correctly_saves_account() {
        // Arrange
        val createAccount = AccountDto.Create(
            email = "test@email.com",
            password = "1234",
            nickname = "testName"
        )

        // Act
        accountService.createAccount(createAccount)

        // Assert
        assertThat(accountRepository.findAll()).hasSize(1)
        val actual = accountRepository.findAll()[0]
        assertThat(actual.email).isEqualTo("test@email.com")
    }

    @Test
    @DisplayName("유저 탈퇴 테스트")
    fun withdraw_correctly_updates_account() {
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

        // Act
        accountService.withdrawAccount(account.accountId, jwtInfoByUser)

        // Assert
        assertThat(accountRepository.findAll()).hasSize(1)
        val actual = accountRepository.findAll()[0]
        assertThat(actual.email).isEqualTo("test@email.com")
        assertThat(actual.withdrawYn).isTrue()
    }

}