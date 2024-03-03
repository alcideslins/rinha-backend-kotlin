package br.com.rinha.model.statement

import java.time.LocalDateTime

data class StatementTransaction(
  val valor: Int,
  val tipo: Char,
  val descricao: String,
  val realizada_em: LocalDateTime
)
