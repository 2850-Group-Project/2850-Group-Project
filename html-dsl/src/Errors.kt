import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.plugins.statuspages.StatusPages
import kotlinx.html.*

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, error ->
            call.respondHtmlTemplate(LayoutTemplate(), HttpStatusCode.InternalServerError) {
                titleText { +"Error: Die Roller" }
                content {
                    h1 { +"500 Internal Server Error" }
                    p { +"${error.message}" }
                }
            }
        }
    }
}
