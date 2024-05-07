package ru.fillit.mdma.dm.persistence.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.fillit.mdma.dm.persistence.entity.base.BaseEntityWithAbstractId
import ru.fillit.mdma.dm.web.dto.types.BalanceOperationResponse
import java.math.BigDecimal
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
class Operation(
    @Id
    @SequenceGenerator(name = "OPERATION_ID_GENERATOR", sequenceName = "OPERATION_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OPERATION_ID_GENERATOR")
    override var id: Long? = null,

    @Enumerated(EnumType.STRING)
    var type: OperationType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number", nullable = false)
    var account: Account,

    var operDate: Long,
    var amount: BigDecimal,
    var description: String
) : BaseEntityWithAbstractId() {

    fun toBalanceOperationResponse(): BalanceOperationResponse = BalanceOperationResponse(
        type = this.type.name,
        accountNumber = this.account.number,
        operDate = Date(this.operDate),
        amount = this.amount.toString(),
        description = this.description
    )
}

enum class OperationType { RECEIPT, EXPENSE }