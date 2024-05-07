package ru.fillit.mdma.dm.persistence.entity.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.fillit.mdma.dm.persistence.entity.Client

@Repository
interface ClientRepository: JpaRepository<Client, Long> {
}