import io.ktor.server.html.Placeholder
import io.ktor.server.html.Template
import io.ktor.server.html.insert
import kotlinx.html.*

class LayoutTemplate : Template<HTML> {
    val titleText = Placeholder<TITLE>()
    val content = Placeholder<FlowContent>()

    override fun HTML.apply() {
        head {
            meta(charset = "utf-8")
            meta(name = "viewport", content = "width=device-width, initial-scale=1")
            meta(name = "color-scheme", content = "light dark")
            link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css")
            title {
                insert(titleText)
            }
        }
        body {
            main(classes = "container") {
                insert(content)
            }
        }
    }
}
