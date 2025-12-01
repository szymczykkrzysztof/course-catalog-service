package com.komy.coursecatalogservice.service

import com.komy.coursecatalogservice.dto.CourseDTO
import com.komy.coursecatalogservice.entity.Course
import com.komy.coursecatalogservice.exception.CourseNotFoundException
import com.komy.coursecatalogservice.repository.CourseRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class CourseService(private val courseRepository: CourseRepository) {
    companion object : KLogging()

    fun addCourse(courseDTO: CourseDTO): CourseDTO {
        val courseEntity = courseDTO.let {
            Course(null, it.name, it.category)
        }
        courseRepository.save(courseEntity)
        logger.info("Course added: $courseEntity")
        return courseEntity.let {
            CourseDTO(it.id, it.name, it.category)
        }
    }

    fun retrieveAllCourses(): List<CourseDTO> {
        val courseDTOS = courseRepository.findAll().map { CourseDTO(it.id, it.name, it.category) }
        return courseDTOS
    }

    fun retrieveCourseById(id: Int): CourseDTO? {
        return courseRepository.findById(id).map { CourseDTO(it.id, it.name, it.category) }.orElse(null)
    }

    fun updateCourse(id: Int, courseDTO: CourseDTO): CourseDTO {
        val existingCourse = courseRepository.findById(id)
        return if (existingCourse.isPresent) {
            existingCourse.get().let {
                it.name = courseDTO.name
                it.category = courseDTO.category
                courseRepository.save(it)
                CourseDTO(it.id, it.name, it.category)
            }
        } else {
            throw CourseNotFoundException("No course found for the passed Id: $id")
        }
    }

    fun deleteCourse(id: Int) {
        val existingCourse = courseRepository.findById(id)
        return if (existingCourse.isPresent) {
            existingCourse.get().let { courseRepository.delete(it) }
        } else {
            throw CourseNotFoundException("No course found for the passed Id: $id")
        }
    }
}


