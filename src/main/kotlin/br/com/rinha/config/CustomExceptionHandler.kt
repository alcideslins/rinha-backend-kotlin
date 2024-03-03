package br.com.rinha.config

import br.com.rinha.error.NotFoundException
import br.com.rinha.error.UnprocessableEntityException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CustomExceptionHandler {

  companion object {
    val NOT_FOUND_EXCEPTION = ResponseEntity.notFound().build<Unit>()
    val UNPROCESSABLE_ENTITY_EXCEPTION = ResponseEntity.unprocessableEntity().build<Unit>()
  }

  @ExceptionHandler(
    NotFoundException::class
  )
  fun notFoundHandler(): ResponseEntity<Unit> {
    return NOT_FOUND_EXCEPTION
  }

  @ExceptionHandler(
    UnprocessableEntityException::class,
    HttpMessageNotReadableException::class
  )
  fun unprocessableEntityHandler(): ResponseEntity<Unit> {
    return UNPROCESSABLE_ENTITY_EXCEPTION
  }
}