package br.com.rinha.model.statement

import java.time.LocalDateTime

data class ClientReportResponse(
  val total: Int,
  val data_extrato: LocalDateTime,
  val limite: Int
)
