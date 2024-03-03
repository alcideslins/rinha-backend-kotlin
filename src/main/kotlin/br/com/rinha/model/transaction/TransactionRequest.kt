package br.com.rinha.model.transaction

import br.com.rinha.error.RinhaErrors
import java.util.Objects.nonNull


data class TransactionRequest(val tipo: String, val valor: Int, val descricao: String) {

  init {
    validate()
  }

  private fun validate() {
    require(isValidType) { RinhaErrors.UNPROCESSABLE_ENTITY_EXCEPTION }
    require(isValidValue) { RinhaErrors.UNPROCESSABLE_ENTITY_EXCEPTION }
    require(isValidDescription) { RinhaErrors.UNPROCESSABLE_ENTITY_EXCEPTION }
  }

  private val isValidValue: Boolean
    get() = valor > MIN_VALUE

  private val isValidType: Boolean
    get() = CREDIT == tipo || DEBIT == tipo

  private val isValidDescription: Boolean
    get() = nonNull(descricao) && descricao.length <= MAX_DESCRIPTION && descricao.isNotBlank()

  val isCredit: Boolean
    get() = CREDIT == tipo

  companion object {
    const val CREDIT: String = "c"
    const val DEBIT: String = "d"
    const val MAX_DESCRIPTION: Int = 10
    const val MIN_VALUE: Int = 0
  }
}