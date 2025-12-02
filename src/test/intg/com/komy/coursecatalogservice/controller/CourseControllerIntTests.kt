package com.komy.coursecatalogservice.controller

import com.komy.coursecatalogservice.dto.CourseDTO
import com.komy.coursecatalogservice.entity.Course
import com.komy.coursecatalogservice.repository.CourseRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourseControllerIntTests(@Autowired private val courseRepository: CourseRepository) : BaseIntegrationTest() {

    lateinit var token: String

    @BeforeEach
    fun setUp() {
        client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
        token = registerAndLogin()
    }

    @Test
    fun addCourse() {

        val courseDTO = CourseDTO(id = null, "Kotlin", "Programming Language", ownerId = 1)
        val result = client.post().uri("/v1/courses")
            .header("Authorization", "Bearer $token")
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
            listOf(
                Course(id = null, "Kotlin", "Programming Language", ownerId = 1),
                Course(id = null, "Spring Boot", "Framework", ownerId = 1)
            )
        courseRepository.saveAll(courseDTOS)
        val result = client.get().uri("/v1/courses")
            .header("Authorization", "Bearer $token")
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
            .header("Authorization", "Bearer $token")
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
            listOf(
                Course(id = null, "Kotlin", "Programming Language", ownerId = 1),
                Course(id = null, "Spring Boot", "Framework", ownerId = 1)
            )
        courseRepository.saveAll(courses)
        val updateCourseDTO =
            CourseDTO(id = courses[0].id, "Kotlin Updated", "Programming Language Updated", ownerId = 1)

        val createdCourse = client.put().uri("/v1/courses/${courses[0].id}")
            .header("Authorization", "Bearer $token")
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
        val course = Course(id = null, "Kotlin", "Programming Language", ownerId = 1)
        val createdCourse = courseRepository.save(course)
        client.delete().uri("/v1/courses/${createdCourse.id}")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isNoContent

        client.delete().uri("/v1/courses/${createdCourse.id}")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isForbidden
    }
}