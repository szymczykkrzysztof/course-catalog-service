package com.komy.coursecatalogservice.security

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<AppUser, Long> {
    fun findByLogin(login: String): AppUser?
}