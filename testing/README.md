# Testing a Ktor Server

Simple example of how to run tests against the request handling code of a
Ktor server application.

This uses [Kotest][kot] to do the assertions.

Run the application with

    ./gradlew run

Visiting `http://0.0.0.0:7070/` will trigger a "Hello World!" plain text
response. You can also get a personalized greeting, e.g. by visiting
`http://0.0.0.0:7070/hello/Nick`.

Run the tests with

    ./gradlew test


[kot]: https://kotest.io/
