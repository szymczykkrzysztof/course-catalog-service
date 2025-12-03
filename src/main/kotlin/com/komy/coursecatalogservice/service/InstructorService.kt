package com.komy.coursecatalogservice.service

import com.komy.coursecatalogservice.dto.InstructorDTO
import com.komy.coursecatalogservice.entity.Instructor
import com.komy.coursecatalogservice.repository.InstructorRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class InstructorService(private val instructorRepository: InstructorRepository) {
    fun createInstructor(instructorDTO: InstructorDTO): InstructorDTO {
        val instructorEntity = instructorDTO.let {
            Instructor(it.id, it.name)
        }
        instructorRepository.save(instructorEntity)
        return instructorEntity.let {
            InstructorDTO(it.id, it.name)
        }
    }

    fun findByInstructorId(instructorId: Int): Optional<Instructor> {
        return instructorRepository.findById(instructorId)

    }

}
