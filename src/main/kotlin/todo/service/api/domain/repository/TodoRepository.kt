package todo.service.api.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import todo.service.api.domain.model.Todo

interface TodoRepository: JpaRepository<Todo, Long>, QuerydslPredicateExecutor<Todo>, TodoRepositoryCustom {
}