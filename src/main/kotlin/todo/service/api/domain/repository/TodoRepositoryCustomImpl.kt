package todo.service.api.domain.repository

import com.querydsl.core.types.*
import com.querydsl.jpa.impl.JPAQueryFactory
import todo.service.api.domain.model.QAccount
import todo.service.api.domain.model.QTodo
import todo.service.api.domain.model.Todo
import java.time.LocalDateTime
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.OrderSpecifier


class TodoRepositoryCustomImpl(
    private val query: JPAQueryFactory
): TodoRepositoryCustom {
    private val todo = QTodo.todo
    private val account = QAccount.account

    override fun getTodoListByUserId(userId: Long): List<Todo> {
        return query.selectFrom(todo)
            .leftJoin(todo.account, account)
            .fetchJoin()
            .where(todo.accountId.eq(userId))
            .orderBy(createdAtDesc())
            .fetch()
    }

    override fun getRecentTodoByUserId(userId: Long): Todo? {
        val now = LocalDateTime.now()
        val secondsDiff = diffSeconds(Expressions.constant(now), todo.todoAt)

        return query.selectFrom(todo)
            .leftJoin(todo.account, account)
            .fetchJoin()
            .where(todo.accountId.eq(userId)
                .and(todo.todoAt.goe(now))
            )
            .orderBy(secondsDiff)
            .fetchFirst()
    }

    private fun createdAtDesc(): OrderSpecifier<*>? {
        return todo.createdAt.desc()
    }

    private fun diffSeconds(
        dateTime1: Expression<LocalDateTime>,
        dateTime2: Expression<LocalDateTime>
    ): OrderSpecifier<Long>? {
        return Expressions.numberTemplate(Long::class.java, "timestampdiff(SECOND, {0}, {1})", dateTime1, dateTime2).asc()
    }
}