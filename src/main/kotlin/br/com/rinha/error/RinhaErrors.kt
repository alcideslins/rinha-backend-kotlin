package br.com.rinha.error

sealed class NoStackTrace : RuntimeException(null, null, false, false) {
  override fun fillInStackTrace(): Throwable {
    return this
  }
}

class NotFoundException : NoStackTrace()
class UnprocessableEntityException : NoStackTrace()

object RinhaErrors {
  val NOT_FOUND_EXCEPTION: NotFoundException = NotFoundException()
  val UNPROCESSABLE_ENTITY_EXCEPTION: UnprocessableEntityException = UnprocessableEntityException()
}
