package com.komy.coursecatalogservice.controller

import com.komy.coursecatalogservice.dto.CourseDTO
import com.komy.coursecatalogservice.entity.Course
import com.komy.coursecatalogservice.repository.CourseRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourseControllerIntTests(@Autowired private val courseRepository: CourseRepository) {
    @LocalServerPort
    private var port: Int = SpringBootTest.WebEnvironment.RANDOM_PORT.ordinal

    private lateinit var client: WebTestClient

    @BeforeEach
    fun setUp() {
        client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun addCourse() {
        val courseDTO = CourseDTO(id = null, "Kotlin", "Programming Language")
        val result = client.post().uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody!!

        Assertions.assertTrue {
            result.id != null
            result.name == courseDTO.name
            result.category == courseDTO.category
        }
    }

    @Test
    fun getAllCourse() {
        val courseDTOS =
            listOf(Course(id = null, "Kotlin", "Programming Language"), Course(id = null, "Spring Boot", "Framework"))
        courseRepository.saveAll(courseDTOS)
        val result = client.get().uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody!!
        Assertions.assertEquals(courseDTOS.size, result.size)
    }

    @Test
    fun getCourseById() {
        val course = Course(id = null, "Kotlin", "Programming Language")
        val createdCourse = courseRepository.save(course)

        val result = client.get().uri("/v1/courses/${createdCourse.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody!!

        Assertions.assertEquals(createdCourse.id, result.id)

    }

    @Test
    fun updateCourse() {
        val courses =
            listOf(Course(id = null, "Kotlin", "Programming Language"), Course(id = null, "Spring Boot", "Framework"))
        courseRepository.saveAll(courses)
        val updateCourseDTO = CourseDTO(id = courses[0].id, "Kotlin Updated", "Programming Language Updated")

        val createdCourse = client.put().uri("/v1/courses/${courses[0].id}")
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
        val course = Course(id = null, "Kotlin", "Programming Language")
        val createdCourse = courseRepository.save(course)
        client.delete().uri("/v1/courses/${createdCourse.id}")
            .exchange()
            .expectStatus().isNoContent

        client.delete().uri("/v1/courses/${createdCourse.id}")
            .exchange()
            .expectStatus().is5xxServerError
    }
}