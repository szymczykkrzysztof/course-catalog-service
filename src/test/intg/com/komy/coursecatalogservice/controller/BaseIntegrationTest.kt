package com.komy.coursecatalogservice.controller

import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    @LocalServerPort
    lateinit var port: Integer

    lateinit var client: WebTestClient

    @BeforeEach
    fun setup() {
        client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    fun registerAndLogin(username: String = "testuser", password: String = "password"): String {
        // 1. register
        client.post()
            .uri("/api/auth/register")
            .bodyValue(mapOf("username" to username, "password" to password))
            .exchange()
            .expectStatus().is2xxSuccessful

        // 2. login
        val tokenResponse = client.post()
            .uri("/api/auth/login")
            .bodyValue(mapOf("username" to username, "password" to password))
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(Map::class.java)
            .returnResult()
            .responseBody!!

        return tokenResponse["token"] as String
    }
}
