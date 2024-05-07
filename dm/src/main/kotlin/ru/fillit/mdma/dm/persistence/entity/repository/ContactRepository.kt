package ru.fillit.mdma.dm.persistence.entity.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.fillit.mdma.dm.persistence.entity.Contact

@Repository
interface ContactRepository: JpaRepository<Contact, Long> {

    @EntityGraph(attributePaths = ["client"])
    fun findContactsByClientId(clientId: Long): List<Contact>
}