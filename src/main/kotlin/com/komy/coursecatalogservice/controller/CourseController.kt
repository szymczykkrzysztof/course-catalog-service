package com.komy.coursecatalogservice.controller

import com.komy.coursecatalogservice.dto.CourseDTO
import com.komy.coursecatalogservice.service.CourseService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/courses")
class CourseController(private val courseService: CourseService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addCourse(@RequestBody courseDTO: CourseDTO): CourseDTO = courseService.addCourse(courseDTO)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun retrieveAllCourses(): List<CourseDTO> = courseService.retrieveAllCourses()

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun retrieveCourseById(@PathVariable("id") id: Int): CourseDTO? = courseService.retrieveCourseById(id)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateCourse(@PathVariable("id") id: Int, @RequestBody courseDTO: CourseDTO): CourseDTO =
        courseService.updateCourse(id, courseDTO)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCourse(@PathVariable("id") id: Int) = courseService.deleteCourse(id)
}