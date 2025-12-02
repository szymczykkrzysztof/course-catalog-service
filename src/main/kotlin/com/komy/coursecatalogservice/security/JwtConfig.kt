package com.komy.coursecatalogservice.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

val dotenv = io.github.cdimascio.dotenv.dotenv()

@Configuration
class JwtProperties {

    @Bean
    fun jwtSecret(): String =
        dotenv["JWT_SECRET"] ?: throw IllegalStateException("Missing JWT_SECRET")

    @Bean
    fun jwtExpiration(): Long =
        dotenv["JWT_EXPIRATION"]?.toLong() ?: 3600
}
