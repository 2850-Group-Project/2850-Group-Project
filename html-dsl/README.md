# Serving Dynamically Constructed HTML

An application that simulates the rolling of a die (d4, d6, d8, d10,
d12 or d20).

This demonstrates how to use a [DSL][dsl] in Ktor to build an HTML document
dynamically and delivery it to a client. It uses the [kotlinx.html][kxh]
library, in conjunction with Ktor's `respondHtml()` function.

Build and run the server with

    ./amper run

Then visit `http://0.0.0.0:8080/roll/d20` to roll a d20, for example.

The application catches exceptions and issues a custom 500 Internal Server
Error page with details of the exception.


[dsl]: https://ktor.io/docs/server-html-dsl.html
[kxh]: https://github.com/Kotlin/kotlinx.html
