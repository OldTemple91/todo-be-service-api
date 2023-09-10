package todo.service.api.domain.exception

import todo.service.api.web.exception.BizBaseException
import todo.service.api.web.exception.ErrorCode

class TodoNotFoundException: BizBaseException(ErrorCode.TODO_NOT_FOUND)
class TodoOwnerNotMatchException: BizBaseException(ErrorCode.TODO_OWNER_NOT_MATCH)