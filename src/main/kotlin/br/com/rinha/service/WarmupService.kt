package br.com.rinha.service

import br.com.rinha.model.statement.StatementResponse
import br.com.rinha.model.transaction.TransactionRequest
import br.com.rinha.model.transaction.TransactionRequest.Companion.CREDIT
import br.com.rinha.model.transaction.TransactionRequest.Companion.DEBIT
import br.com.rinha.model.transaction.TransactionResponse
import br.com.rinha.repository.RegisterTransactionRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException.*
import org.springframework.web.client.RestTemplate
import java.util.stream.IntStream
import kotlin.time.measureTime

@Component
class WarmupService(
  val restTemplate: RestTemplate,
  val registerRepository: RegisterTransactionRepository,
  @Value("\${warmup.host}") val warmupHost: String,
  @Value("\${warmup.enabled}") val warmupEnabled: Boolean,
  @Value("\${warmup.requests}") val warmupRequests: Int
) : ApplicationListener<ApplicationReadyEvent> {

  private val registerUrl: String = "$warmupHost/clientes/{id}/transacoes"
  private val statementUrl: String = "$warmupHost/clientes/{id}/extrato"

  override fun onApplicationEvent(event: ApplicationReadyEvent) {
    if (warmupEnabled) {
      val duration = measureTime {
        executeAllRequests()
      }

      LOGGER.info("Warmup completed in {}", duration)
    }
  }

  private fun executeAllRequests() {
    LOGGER.info("Starting warmup...")

    IntStream.of(warmupRequests).parallel().forEach {
      IntStream.rangeClosed(4, 10).parallel().forEach { id ->
        executeStatement(id)
        executeTransaction(TransactionRequest(CREDIT, 1000, "credit"), id)
        executeTransaction(TransactionRequest(DEBIT, 1000, "debit"), id)
      }
    }

    registerRepository.resetDb()
    System.gc()

    LOGGER.info("Finished warmup")
  }

  private fun executeStatement(id: Int) {
    try {
      restTemplate.getForObject(statementUrl, StatementResponse::class.java, id)
    } catch (ex: Exception) {
      handleError(ex)
    }
  }

  private fun executeTransaction(request: TransactionRequest, id: Int) {
    try {
      restTemplate.postForObject(registerUrl, request, TransactionResponse::class.java, id)
    } catch (ex: Exception) {
      handleError(ex)
    }
  }

  private fun handleError(ex: Exception) {
    when (ex) {
      is BadRequest, is NotFound, is UnprocessableEntity -> return
      else -> LOGGER.error("Error on warmup request", ex)
    }
  }

  companion object {
    private val LOGGER: Logger = LoggerFactory.getLogger(WarmupService::class.java)
  }

}
