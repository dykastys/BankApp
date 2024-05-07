package ru.fillit.mdma.dm.errors

open class BadRequestException(override val message: String?): RuntimeException(message)
open class ClientsNotFound(override val message: String?): RuntimeException(message)
