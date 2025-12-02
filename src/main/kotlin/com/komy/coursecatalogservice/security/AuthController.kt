package com.komy.coursecatalogservice.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val registrationService: RegistrationService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): AuthResponse {
        val authToken = UsernamePasswordAuthenticationToken(request.username, request.password)
        authenticationManager.authenticate(authToken)

        val user = userRepository.findByLogin(request.username)
            ?: throw RuntimeException("User not found")

        val token = jwtService.generateToken(user)
        return AuthResponse(token)
    }
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): RegisterResponse =
        registrationService.register(request)
}

data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String)