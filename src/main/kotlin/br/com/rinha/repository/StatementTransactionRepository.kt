package br.com.rinha.repository

import br.com.rinha.error.RinhaErrors
import br.com.rinha.model.statement.ClientReportResponse
import br.com.rinha.model.statement.StatementResponse
import br.com.rinha.model.statement.StatementTransaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowCallbackHandler
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDateTime
import java.util.*
import java.util.Objects.isNull

@Repository
class StatementTransactionRepository @Autowired constructor(private val jdbcTemplate: NamedParameterJdbcTemplate) {

  fun findStatement(clientId: Int): StatementResponse {
    val params = MapSqlParameterSource().addValue(PARAM_CLIENT_ID, clientId)

    val statementRowHandler = StatementRowHandler()
    jdbcTemplate.query(QUERY_STATEMENT_TRANSACTION, params, statementRowHandler)
    return statementRowHandler.statementResponse
  }

  companion object {
    private const val QUERY_STATEMENT_TRANSACTION = """
      SELECT c.balance, c.lmt, t.value, t.type, t.created_at, t.description
      FROM client c
      LEFT JOIN LATERAL (
          SELECT t.value, t.type, t.created_at, t.description
          FROM transaction t
          WHERE t.client_id = c.id
          ORDER BY t.created_at DESC
          LIMIT 10
      ) t ON TRUE
      WHERE c.id = :clientId
    """

    private const val PARAM_CLIENT_ID = "clientId"
  }
}


internal class StatementRowHandler : RowCallbackHandler {
  private var balance: ClientReportResponse? = null
  private val transactions: List<StatementTransaction> = mutableListOf()

  @Throws(SQLException::class)
  override fun processRow(rs: ResultSet) {

    if (isNull(balance)) {
      balance = ClientReportResponse(rs.getInt(RESP_BALANCE), LocalDateTime.now(), rs.getInt(RESP_LIMIT))
    }

    val type: String = rs.getString(RESP_TYPE) ?: ""

    if (type.isNotEmpty()) {
      addTransaction(
        StatementTransaction(
          rs.getInt(RESP_VALUE),
          type[0],
          rs.getString(RESP_DESCRIPTION),
          rs.getTimestamp(RESP_CREATED_AT).toLocalDateTime()
        )
      )
    }
  }

  val statementResponse: StatementResponse
    get() = balance?.let { StatementResponse(it, transactions) } ?: throw RinhaErrors.NOT_FOUND_EXCEPTION

  private fun addTransaction(transaction: StatementTransaction) {
    transactions.addLast(transaction)
  }

  companion object {
    private const val RESP_BALANCE = "balance"
    private const val RESP_LIMIT = "lmt"
    private const val RESP_VALUE = "value"
    private const val RESP_TYPE = "type"
    private const val RESP_DESCRIPTION = "description"
    private const val RESP_CREATED_AT = "created_at"
  }
}


