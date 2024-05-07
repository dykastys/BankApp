package ru.fillit.mdma.dm.persistence.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.fillit.mdma.dm.persistence.entity.base.BaseEntityWithoutAbstractId
import ru.fillit.mdma.dm.web.dto.types.AccountResponse
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
class Account(
    @Id
    @Column(nullable = false, unique = true)
    var number: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    var client: Client?,

    @OneToMany(mappedBy = "account")
    var operations: List<Operation>,

    @Enumerated(EnumType.STRING)
    var type: AccountType,
    @Enumerated(EnumType.STRING)
    var currency: Currency,
    @Enumerated(EnumType.STRING)
    var status: AccountStatus,
    var openDate: Long,
    var closeDate: Long? = null,
    var deferment: Int? = null
) : BaseEntityWithoutAbstractId() {

    fun toAccountResponse(): AccountResponse = AccountResponse(
        number = this.number,
        clientId = this.client?.id.toString(),
        type = this.type.name,
        currency = this.currency.name,
        status = this.status.name,
        openDate = Date(this.openDate),
        closeDate = getCloseDateOrNull(),
        deferment = this.deferment?.toString(),
        shortcut = this.number.substring(this.number.length-4)
    )

    private fun getCloseDateOrNull(): Date? = if(closeDate != null) { Date(closeDate!!) } else { null }
}

enum class AccountType { PAYMENT, BUDGET, TRANSIT, OVERDRAFT }
enum class Currency { RUR }
enum class AccountStatus { INACTIVE, ACTIVE, LOCKED, CLOSED }