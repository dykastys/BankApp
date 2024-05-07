package ru.fillit.mdma.dm.persistence.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.fillit.mdma.dm.persistence.entity.base.BaseEntityWithAbstractId
import ru.fillit.mdma.dm.web.dto.types.ClientResponse
import java.util.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("passport_series", "passport_number", "inn"))])
@EntityListeners(AuditingEntityListener::class)
class Client(
    @Id
    @SequenceGenerator(
        name = "CLIENT_ID_GENERATOR", sequenceName = "CLIENT_ID_SEQ", allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLIENT_ID_GENERATOR")
    override var id: Long? = null,

    @OneToMany(mappedBy = "client")
    var accounts: Set<Account>?,

    @OneToMany(mappedBy = "client")
    var contacts: Set<Contact>,

    var firstName: String,
    var lastName: String,
    var patronymic: String? = null,
    var birthDate: Long,
    var passportSeries: String,
    var passportNumber: String,
    var inn: String,

    var address: String? = null
) : BaseEntityWithAbstractId() {

    fun toClientResponse(): ClientResponse = ClientResponse(
            id = this.id.toString(),
            lastName = this.lastName,
            firstName = this.firstName,
            patronymic = this.patronymic,
            birthDate = Date(this.birthDate),
            passportSeries = this.passportSeries,
            passportNumber = this.passportNumber,
            inn = this.inn,
            address = this.address
        )
}