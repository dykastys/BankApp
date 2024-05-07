package ru.fillit.mdma.dm.persistence.entity.base

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.util.ProxyUtils

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntityWithAbstractId : BaseEntityWithoutAbstractId() {

    abstract var id: Long?

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (this === other) return true
        if (javaClass != ProxyUtils.getUserClass(other)) return false

        other as BaseEntityWithAbstractId

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = 33

    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id)"
    }
}