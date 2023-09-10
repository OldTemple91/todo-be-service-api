package todo.service.api.domain.repository

import com.querydsl.core.types.Predicate
import todo.service.api.domain.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.*

interface AccountRepository: JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
    override fun findOne(predicate: Predicate): Optional<Account>

    fun findOneByEmailIgnoreCase(login: String): Optional<Account>
}