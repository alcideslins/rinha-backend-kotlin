package br.com.rinha.model.statement

data class StatementResponse(
  val saldo: ClientReportResponse,
  val ultimas_transacoes: List<StatementTransaction>
)
