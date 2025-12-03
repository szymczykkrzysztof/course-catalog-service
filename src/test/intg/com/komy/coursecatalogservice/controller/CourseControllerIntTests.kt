package com.komy.coursecatalogservice.controller

import com.komy.coursecatalogservice.dto.CourseDTO
import com.komy.coursecatalogservice.entity.Course
import com.komy.coursecatalogservice.entity.Instructor
import com.komy.coursecatalogservice.repository.CourseRepository
import com.komy.coursecatalogservice.repository.InstructorRepository
import com.komy.coursecatalogservice.util.PostgreSQLContainerInitializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourseControllerIntTests(
    @Autowired private val courseRepository: CourseRepository,
    @Autowired private val instructorRepository: InstructorRepository
) : PostgreSQLContainerInitializer() {
    @LocalServerPort
    private var port: Int = SpringBootTest.WebEnvironment.RANDOM_PORT.ordinal

    private lateinit var client: WebTestClient

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        instructorRepository.deleteAll()
        client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
        val instructor = Instructor(id = null, name = "Krzysztof")
        instructorRepository.save(instructor)
    }

    @Test
    fun addCourse() {
        val courseDTO =
            CourseDTO(id = null, "Kotlin", "Programming Language", instructorRepository.findAll().first().id)
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
        val instructor = instructorRepository.findAll().first()
        val courseDTOS =
            listOf(
                Course(id = null, "Kotlin", "Programming Language", instructor = instructor),
                Course(id = null, "Spring Boot", "Framework", instructor = instructor)
            )
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
    fun getAllCourse_ByName() {
        val instructor = instructorRepository.findAll().first()
        val courseDTOS =
            listOf(
                Course(id = null, "Kotlin", "Programming Language", instructor = instructor),
                Course(id = null, "Spring Boot", "Framework", instructor = instructor)
            )
        courseRepository.saveAll(courseDTOS)
        val uri = UriComponentsBuilder.fromUriString("/v1/courses")
            .queryParam("course_name", "Kotlin")
            .toUriString()
        val result = client.get().uri(uri)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody!!
        Assertions.assertEquals(1, result.size)
    }

    @Test
    fun getCourseById() {
        val instructor = instructorRepository.findAll().first()
        val course = Course(id = null, "Kotlin", "Programming Language", instructor)
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
        val instructor = instructorRepository.findAll().first()
        val courses =
            listOf(
                Course(id = null, "Kotlin", "Programming Language", instructor),
                Course(id = null, "Spring Boot", "Framework", instructor)
            )
        courseRepository.saveAll(courses)
        val updateCourseDTO =
            CourseDTO(
                id = courses[0].id,
                "Kotlin Updated",
                "Programming Language Updated",
                instructorId = instructor.id
            )

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
        val instructor = instructorRepository.findAll().first()
        val course = Course(id = null, "Kotlin", "Programming Language", instructor)
        val createdCourse = courseRepository.save(course)
        client.delete().uri("/v1/courses/${createdCourse.id}")
            .exchange()
            .expectStatus().isNoContent

        client.delete().uri("/v1/courses/${createdCourse.id}")
            .exchange()
            .expectStatus().is5xxServerError
    }
}