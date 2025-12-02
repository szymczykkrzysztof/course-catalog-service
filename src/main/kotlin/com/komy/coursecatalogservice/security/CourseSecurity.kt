package com.komy.coursecatalogservice.security

import com.komy.coursecatalogservice.repository.CourseRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class CourseSecurity(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository
) {

    fun isOwner(courseId: Int, authentication: Authentication): Boolean {
        val username = authentication.name
        val user = userRepository.findByLogin(username) ?: return false
        val course = courseRepository.findById(courseId).orElse(null) ?: return false

        return course.ownerId == user.id
    }
}