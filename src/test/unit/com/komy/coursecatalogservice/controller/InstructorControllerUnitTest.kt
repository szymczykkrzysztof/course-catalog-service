package com.komy.coursecatalogservice.controller

import com.komy.coursecatalogservice.dto.InstructorDTO
import com.komy.coursecatalogservice.exceptionhandler.GlobalErrorHandler
import com.komy.coursecatalogservice.service.InstructorService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

@WebMvcTest(controllers = [InstructorController::class])
@ActiveProfiles("test")
class InstructorControllerUnitTest {
    @MockkBean
    lateinit var instructorServiceMockk: InstructorService
    private lateinit var client: WebTestClient

    @BeforeEach
    fun setUp() {
        val controller = InstructorController(instructorServiceMockk)
        client = WebTestClient.bindToController(controller)
            .controllerAdvice(GlobalErrorHandler())
            .build()
    }

    @Test
    fun addInstructor() {
        val instructorDto = InstructorDTO(id = null, "Krzysztof")
        every { instructorServiceMockk.createInstructor(any()) } returns instructorDto.copy(id = 1)
        val result = client.post().uri("/v1/instructors")
            .bodyValue(instructorDto)
            .exchange()
            .expectStatus().isCreated
            .expectBody(InstructorDTO::class.java)
            .returnResult()
            .responseBody!!
        Assertions.assertTrue {
            result.id != null
            result.name == instructorDto.name
        }
    }

}