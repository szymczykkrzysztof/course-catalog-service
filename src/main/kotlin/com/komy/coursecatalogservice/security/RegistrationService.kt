package com.komy.coursecatalogservice.security

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    fun register(request: RegisterRequest): RegisterResponse {
        if (userRepository.findByLogin(request.username) != null) {
            throw IllegalArgumentException("User already exists")
        }

        val user = AppUser(
            login = request.username,
            hashedPassword = passwordEncoder.encode(request.password),
            roles = setOf(Role.ADMIN)  // domyślna rola Admin
        )

        val saved = userRepository.save(user)
        val token = jwtService.generateToken(saved)

        return RegisterResponse(
            id = saved.id!!,
            username = saved.username,
            roles = saved.roles,
            token = token
        )
    }
}