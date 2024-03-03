package br.com.rinha.controller

import br.com.rinha.model.statement.StatementResponse
import br.com.rinha.model.transaction.TransactionRequest
import br.com.rinha.model.transaction.TransactionResponse
import br.com.rinha.service.TransactionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientes")
class TransactionController(private val service: TransactionService) {

  @PostMapping("/{id}/transacoes")
  fun transaction(@PathVariable id: Int, @RequestBody transactionRequest: TransactionRequest): TransactionResponse? {
    return service.register(id, transactionRequest)
  }

  @GetMapping("/{id}/extrato")
  fun statement(@PathVariable id: Int): StatementResponse {
    return service.list(id)
  }
}
