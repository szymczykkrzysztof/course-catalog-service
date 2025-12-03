package com.komy.coursecatalogservice.repository

import com.komy.coursecatalogservice.entity.Instructor
import org.springframework.data.repository.CrudRepository

interface InstructorRepository : CrudRepository<Instructor, Int>