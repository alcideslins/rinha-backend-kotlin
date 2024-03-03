package br.com.rinha.repository

import br.com.rinha.error.RinhaErrors.NOT_FOUND_EXCEPTION
import br.com.rinha.error.RinhaErrors.UNPROCESSABLE_ENTITY_EXCEPTION
import br.com.rinha.model.transaction.TransactionResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException

@Repository
class RegisterTransactionRepository @Autowired constructor(private val jdbcTemplate: NamedParameterJdbcTemplate) {

  fun registerCredit(clientId: Int, value: Int, description: String): TransactionResponse? =
    registerTransaction(clientId, value, description, QUERY_REGISTER_CREDIT)

  fun registerDebit(clientId: Int, value: Int, description: String): TransactionResponse? =
    registerTransaction(clientId, value, description, QUERY_REGISTER_DEBIT)


  private fun registerTransaction(clientId: Int, value: Int, description: String, query: String): TransactionResponse? {
    val params: MutableMap<String, Any> = mutableMapOf(
      PARAM_CLIENT_ID to clientId,
      PARAM_VALUE to value,
      PARAM_DESCRIPTION to description
    )
    return jdbcTemplate.queryForObject(
      query, params
    ) { rs: ResultSet, _: Int -> this.mapTransactionResponse(rs) }
  }

  @Throws(SQLException::class)
  private fun mapTransactionResponse(rs: ResultSet): TransactionResponse {
    checkStatus(rs.getString(RESP_STATUS)[0])
    return TransactionResponse(
      rs.getInt(RESP_LIMIT),
      rs.getInt(RESP_BALANCE)
    )
  }

  private fun checkStatus(status: Char) {
    when (status) {
      STATUS_SUCCESS -> return
      STATUS_NOT_FOUND -> throw NOT_FOUND_EXCEPTION
      STATUS_NO_LIMIT -> throw UNPROCESSABLE_ENTITY_EXCEPTION
    }
  }

  fun resetDb() {
    jdbcTemplate.execute(QUERY_RESET_DB) { preparedStatement ->
      preparedStatement.execute()
    }
  }

  companion object {
    private const val QUERY_REGISTER_CREDIT = "SELECT * FROM register_credit(:clientId, :value, :description)"
    private const val QUERY_REGISTER_DEBIT = "SELECT * FROM register_debit(:clientId, :value, :description)"
    private const val QUERY_RESET_DB = "SELECT reset_database()"

    private const val PARAM_CLIENT_ID = "clientId"
    private const val PARAM_VALUE = "value"
    private const val PARAM_DESCRIPTION = "description"

    private const val RESP_LIMIT = "r_lmt"
    private const val RESP_BALANCE = "r_balance"
    private const val RESP_STATUS = "r_status"

    private const val STATUS_SUCCESS = 'S'
    private const val STATUS_NOT_FOUND = 'N'
    private const val STATUS_NO_LIMIT = 'L'
  }
}


