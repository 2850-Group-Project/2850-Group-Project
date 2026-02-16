import io.ktor.server.application.Application

fun Application.module() {
    configureErrorHandling()
    configureRouting()
}

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
