package todo.service.api.domain.exception

import todo.service.api.web.exception.BizBaseException
import todo.service.api.web.exception.ErrorCode


class PasswordNotMatchException : BizBaseException(ErrorCode.PASSWORD_NOT_MATCH)
class EmailNotFoundException: BizBaseException(ErrorCode.EMAIL_NOT_FOUND)
class WithdrawAccountException: BizBaseException(ErrorCode.WITHDRAW_ACCOUNT)
class DuplicateAccountException: BizBaseException(ErrorCode.DUPLICATE_ACCOUNT)
class AccountNotFoundException: BizBaseException(ErrorCode.ACCOUNT_NOT_FOUND)