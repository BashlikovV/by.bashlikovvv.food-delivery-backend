package by.bashlikovvv

import by.bashlikovvv.api.controller.configureRouting
import by.bashlikovvv.api.controller.configureSerialization
import by.bashlikovvv.di.appModule
import by.bashlikovvv.di.dataModule
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        modules(dataModule, appModule)
    }
    configureSerialization()
    configureRouting()
}
