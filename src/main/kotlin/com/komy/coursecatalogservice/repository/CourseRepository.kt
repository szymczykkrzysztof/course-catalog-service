package com.komy.coursecatalogservice.repository

import com.komy.coursecatalogservice.entity.Course
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<Course, Int> {
}