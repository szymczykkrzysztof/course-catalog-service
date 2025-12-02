package com.komy.coursecatalogservice.repository

import com.komy.coursecatalogservice.entity.Course
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<Course, Int> {
    fun findByNameContaining(courseName: String): List<Course>

    @Query("SELECT * FROM COURSES  where NAME like %?1%", nativeQuery = true)
    fun findCoursesByName(courseName: String): List<Course>
}