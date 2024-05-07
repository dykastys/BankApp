package ru.fillit.mdma.dm.audit

import org.springframework.security.core.context.SecurityContextHolder.getContext

fun getAuthenticationIdentityFromContext(): String =
    getContext().authentication?.name ?: ""