package todo.service.api.domain.service

import todo.service.api.domain.exception.DuplicateAccountException
import todo.service.api.domain.exception.EmailNotFoundException
import todo.service.api.domain.model.Account
import todo.service.api.domain.repository.AccountRepository
import todo.service.api.dto.AccountDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import todo.service.api.domain.exception.AccountNotFoundException
import todo.service.api.security.JwtLoginAccount
import todo.service.api.web.exception.BadInputException

@Service
@Transactional
class AccountService(
    private val accountRepository: AccountRepository
) {
    fun getAccounts(): List<AccountDto> {
        return accountRepository.findAll().map { AccountDto(it) }
    }

    private fun getAccount(id: Long): Account {
        return accountRepository.findById(id).orElseThrow { AccountNotFoundException() }
    }

    fun getAccountById(id: Long): AccountDto {
        return AccountDto(getAccount(id))
    }

    fun getAccountByEmail(email: String): Account {
        return accountRepository.findOneByEmailIgnoreCase(email).orElseThrow { EmailNotFoundException() }

    }

    fun createAccount(create: AccountDto.Create) {
        val existingUser = accountRepository.findOneByEmailIgnoreCase(create.email)
        if(existingUser.isPresent) {
            throw DuplicateAccountException()
        }
        val account = Account.create(
            email = create.email,
            password = create.password,
            nickname = create.nickname
        )

        accountRepository.save(account)
    }

    fun withdrawAccount(id: Long, jwtAccount: JwtLoginAccount) {
        val account = getAccount(id)

        if(jwtAccount.accountId != account.accountId) {
            throw BadInputException()
        }

        account.withdraw()
        accountRepository.save(account)
    }

}