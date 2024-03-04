package br.com.rinha.service

import br.com.rinha.model.statement.StatementResponse
import br.com.rinha.model.transaction.TransactionRequest
import br.com.rinha.model.transaction.TransactionResponse
import br.com.rinha.repository.RegisterTransactionRepository
import br.com.rinha.repository.StatementTransactionRepository
import org.springframework.stereotype.Service

@Service
class TransactionService(
  val registerRepository: RegisterTransactionRepository,
  val statementRepository: StatementTransactionRepository
) {

  fun register(id: Int, transactionRequest: TransactionRequest): TransactionResponse? {
    return when {
      transactionRequest.isCredit -> registerRepository
        .registerCredit(id, transactionRequest.valor, transactionRequest.descricao)

      else -> registerRepository.registerDebit(id, transactionRequest.valor, transactionRequest.descricao)
    }
  }

  fun list(id: Int): StatementResponse {
    return statementRepository.findStatement(id)
  }
}
