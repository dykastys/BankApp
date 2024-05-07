package ru.fillit.mdma.dm.persistence.entity

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.fillit.mdma.dm.persistence.entity.base.BaseEntityWithAbstractId
import ru.fillit.mdma.dm.persistence.entity.base.BaseEntityWithoutAbstractId
import ru.fillit.mdma.dm.web.dto.types.BalanceAmountResponse
import java.math.BigDecimal

@Entity
@EntityListeners(AuditingEntityListener::class)
class AccountBalance(
    @Id
    val accountNumber: String,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    var account: Account?,

    var balanceDate: Long,
    var amount: BigDecimal
) : BaseEntityWithoutAbstractId() {

    fun toBalanceAmountResponse(): BalanceAmountResponse = BalanceAmountResponse(amount.toString())
}