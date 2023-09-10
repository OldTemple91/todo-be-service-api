package todo.service.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories
@SpringBootApplication
class TodoServiceApplication

fun main(args: Array<String>) {
    runApplication<TodoServiceApplication>(*args)
}