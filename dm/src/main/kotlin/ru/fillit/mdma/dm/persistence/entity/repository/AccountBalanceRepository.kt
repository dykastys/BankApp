package ru.fillit.mdma.dm.persistence.entity.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.fillit.mdma.dm.persistence.entity.AccountBalance

interface AccountBalanceRepository: JpaRepository<AccountBalance, String> {

    @Query("""
        select ab.amount
        from AccountBalance ab
        where ab.accountNumber = :accountNumber
    """)
    fun findAccountBalanceByAccountNumber(accountNumber: String): String
}