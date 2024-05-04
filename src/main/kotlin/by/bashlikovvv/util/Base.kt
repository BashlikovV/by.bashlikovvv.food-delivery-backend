package by.bashlikovvv.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }

suspend inline fun ApplicationCall.respond(
    isCorrect: () -> Boolean,
    crossinline onCorrect: suspend ApplicationCall.() -> Pair<HttpStatusCode, Any>,
    crossinline onIncorrect: suspend ApplicationCall.() -> Pair<HttpStatusCode, Any>
) {
    if (isCorrect.invoke()) {
        val result = onCorrect()
        respond(result.first, result.second)
    } else {
        val result = onIncorrect()
        respond(result.first, result.second)
    }
}

suspend fun <T>PipelineContext<*, ApplicationCall>.getWithCheck(
    creator: suspend PipelineContext<*, ApplicationCall>.() -> T
): T? {
    return try {
        creator()
    } catch (e: Exception) {
        null
    }
}

