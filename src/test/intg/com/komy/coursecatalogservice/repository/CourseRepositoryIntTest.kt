package com.komy.coursecatalogservice.repository

import com.komy.coursecatalogservice.entity.Course
import com.komy.coursecatalogservice.entity.Instructor
import com.komy.coursecatalogservice.util.PostgreSQLContainerInitializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Stream

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class CourseRepositoryIntTest : PostgreSQLContainerInitializer(){
    @Autowired
    lateinit var courseRepository: CourseRepository
    @Autowired lateinit var instructorRepository: InstructorRepository
    @BeforeEach
    fun setUp() {
        val instructor = Instructor(id = null, name = "Krzysztof")
        instructorRepository.save(instructor)
        val courses =
            listOf(
                Course(id = null, "Kotlin Language", "Programming Language", instructor = instructor),
                Course(id = null, "React Language", "Framework",instructor)
            )
        courseRepository.saveAll(courses)
    }

    @Test
    fun findByNameContaining() {
        val result = courseRepository.findByNameContaining("Language")
        println(result)
        Assertions.assertEquals(2, result.size)
    }

    @Test
    fun findCoursesByName() {
        val result = courseRepository.findCoursesByName("Language")
        println(result)
        Assertions.assertEquals(2, result.size)
    }

    @ParameterizedTest
    @MethodSource("languageAndSize")
    fun findCoursesByNameAnotherApproach(name: String, size: Int) {
        val result = courseRepository.findCoursesByName(name)
        println(result)
        Assertions.assertEquals(size, result.size)
    }

    companion object {
        @JvmStatic
        fun languageAndSize(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("Language", 2),
                Arguments.of("Kotlin", 1),
                Arguments.of("React", 1)
            )
        }
    }

}