package com.komy.coursecatalogservice.controller

import com.komy.coursecatalogservice.dto.CourseDTO
import com.komy.coursecatalogservice.exceptionhandler.GlobalErrorHandler
import com.komy.coursecatalogservice.service.CourseService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@WebMvcTest(controllers = [CourseController::class])
@ActiveProfiles("test")
class CourseControllerUnitTest {
    @MockkBean
    lateinit var courseServiceMockk: CourseService
    private lateinit var client: WebTestClient

    @BeforeEach
    fun setUp() {
        val controller = CourseController(courseServiceMockk)
        client = WebTestClient.bindToController(controller)
            .controllerAdvice(GlobalErrorHandler())
            .build()
    }

    @Test
    fun addCourse() {
        val courseDTO = CourseDTO(id = null, "Kotlin", "Programming Language", instructorId = 1)
        every { courseServiceMockk.addCourse(any()) } returns courseDTO.copy(id = 1)
        val result = client.post().uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody!!

        Assertions.assertAll(
            { Assertions.assertNotNull(result.id) },
            { Assertions.assertEquals(courseDTO.name, result.name) },
            { Assertions.assertEquals(courseDTO.category, result.category) }
        )
    }

    @Test
    fun addCourse_validation() {
        val courseDTO = CourseDTO(id = null, "", "", instructorId = null)
        every { courseServiceMockk.addCourse(any()) } returns courseDTO.copy(id = 1)
        val result = client.post().uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody
        Assertions.assertEquals("courseDTO.category must not be blank, courseDTO.instructorId must not be null, courseDTO.name must not be blank", result)

    }

    @Test
    fun addCourse_runtimeException() {
        val courseDTO = CourseDTO(id = null, "Kotlin", "Programming Language", instructorId = 1)
        val message="Unexpected error occurred while processing request"
        every { courseServiceMockk.addCourse(any()) } throws RuntimeException(message)
        val result = client.post().uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(String::class.java)
            .returnResult()
            .responseBody
        Assertions.assertEquals(message, result)

    }


    @Test
    fun getAllCourse() {
        every { courseServiceMockk.retrieveAllCourses(any()) }.returnsMany(
            listOf(
                CourseDTO(
                    1,
                    "Kotlin",
                    "Programming Language",
                    instructorId = 1
                ),
                CourseDTO(
                    2,
                    "React",
                    "Programming Language",
                    instructorId = 1
                )
            )
        )
        val result = client.get().uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody!!
        Assertions.assertEquals(2, result.size)
    }

    @Test
    fun updateCourse() {
        every { courseServiceMockk.updateCourse(any(), any()) } returns CourseDTO(
            100,
            "Kotlin Updated",
            "Programming Language Updated",
            instructorId = 1
        )
        val updateCourseDTO = CourseDTO(id = 100, "Kotlin Updated", "Programming Language Updated", instructorId = 1)

        val createdCourse = client.put().uri("/v1/courses/100")
            .bodyValue(updateCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(updateCourseDTO, createdCourse)
    }

    @Test
    fun deleteCourse() {
        every { courseServiceMockk.deleteCourse(any()) } just runs
        client.delete().uri("/v1/courses/100")
            .exchange()
            .expectStatus().isNoContent
    }
}