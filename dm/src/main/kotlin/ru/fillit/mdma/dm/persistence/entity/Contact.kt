package ru.fillit.mdma.dm.persistence.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.fillit.mdma.dm.persistence.entity.base.BaseEntityWithAbstractId
import ru.fillit.mdma.dm.web.dto.types.ContactResponse

@Entity
@EntityListeners(AuditingEntityListener::class)
class Contact(
    @Id
    @SequenceGenerator(name = "CONTACT_ID_GENERATOR", sequenceName = "CONTACT_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTACT_ID_GENERATOR")
    override var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    var client: Client,

    @Enumerated(EnumType.STRING)
    var type: ContactType,
    var value: String
) : BaseEntityWithAbstractId() {

    fun toContactResponse(): ContactResponse = ContactResponse(
        id = this.id.toString(),
        clientId = this.client.id.toString(),
        type = this.type.name,
        value = this.value,
        shortcut = calculateShortcut()
    )

    private fun calculateShortcut(): String =
        when(type) {
            ContactType.PHONE -> value.substring(value.length-4)
            else ->  value.substring(value.indexOf('@')-1)
        }
}

enum class ContactType { PHONE, EMAIL }