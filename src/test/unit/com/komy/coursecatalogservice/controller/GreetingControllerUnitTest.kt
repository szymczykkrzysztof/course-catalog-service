package com.komy.coursecatalogservice.controller

import com.komy.coursecatalogservice.service.GreetingsService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

@WebMvcTest(controllers = [GreetingController::class])
@ActiveProfiles("test")
class GreetingControllerUnitTest {
    @MockkBean
    lateinit var greetingsServiceMock: GreetingsService
    private lateinit var client: WebTestClient

    @BeforeEach
    fun setUp() {
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