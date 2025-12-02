package com.komy.coursecatalogservice.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*

@Service
class JwtService(
    @Qualifier("jwtSecret") private val secret: String,
    @Qualifier("jwtExpiration") private val expirationSeconds: Long
) {

    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun generateToken(user: UserDetails): String =
        Jwts.builder()
            .subject(user.username)
            .claim("roles", user.authorities.map { it.authority })
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationSeconds * 1000))
            .signWith(key)
            .compact()

    fun extractUsername(token: String): String =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject

    fun validateToken(token: String, user: UserDetails): Boolean =
        extractUsername(token) == user.username
}