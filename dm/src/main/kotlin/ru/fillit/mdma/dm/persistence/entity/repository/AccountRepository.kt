package ru.fillit.mdma.dm.persistence.entity.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.fillit.mdma.dm.persistence.entity.Account

@Repository
interface AccountRepository: JpaRepository<Account, String> {

    @EntityGraph(attributePaths = ["client"])
    fun findAccountsByClientId(clientId: Long): List<Account>
}