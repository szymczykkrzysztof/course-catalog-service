package com.komy.coursecatalogservice.controller

import com.komy.coursecatalogservice.dto.CourseDTO
import com.komy.coursecatalogservice.service.CourseService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/courses")
@Validated
class CourseController(private val courseService: CourseService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addCourse(@RequestBody @Valid courseDTO: CourseDTO): CourseDTO = courseService.addCourse(courseDTO)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun retrieveAllCourses(@RequestParam("course_name", required = false) courseName:String?): List<CourseDTO> = courseService.retrieveAllCourses(courseName)

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