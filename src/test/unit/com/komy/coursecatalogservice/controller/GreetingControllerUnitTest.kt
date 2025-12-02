package com.komy.coursecatalogservice.controller

import com.komy.coursecatalogservice.service.GreetingsService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

@ActiveProfiles("test")
class GreetingControllerUnitTest {
    lateinit var greetingsServiceMock: GreetingsService
    private lateinit var client: WebTestClient

    @BeforeEach
    fun setUp() {
        // inicjalizacja mocka, aby uniknąć błędu lateinit property ... has not been initialized
        greetingsServiceMock = mockk()
        val controller = GreetingController(greetingsServiceMock)
        client = WebTestClient.bindToController(controller).build()
    }
    @Test
    fun retrieveGreeting() {
        val name = "Krzysztof"
        every { greetingsServiceMock.retrieveGreeting(name) } returns "$name, Hello from default profile"

        val result = client.get().uri("/v1/greetings/{name}", name)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(String::class.java)
            .returnResult()
        Assertions.assertEquals("$name, Hello from default profile", result.responseBody)
    }
}