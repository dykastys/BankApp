package ru.fillit.mdma.dm.extensions

import ru.fillit.mdma.dm.web.dto.types.SearchClientRequest
import java.time.ZoneId

class Handler {

    companion object {
        fun getMessageByParameters(searchClientRequest: SearchClientRequest): String {
            var message = ""
            if (searchClientRequest.id != null) { message += "id=${searchClientRequest.id} " }
            if (searchClientRequest.firstName != null) { message += "firstName=${searchClientRequest.firstName} " }
            if (searchClientRequest.lastName != null) { message += "lastName=${searchClientRequest.lastName} " }
            if (searchClientRequest.patronymic != null) { message += "patronymic=${searchClientRequest.patronymic} " }
            if (searchClientRequest.birthDate != null) { message += "birthDate=${searchClientRequest.birthDate} " }
            if (searchClientRequest.passport != null) { message += "passport=${searchClientRequest.passport} " }
            if (searchClientRequest.inn != null) { message += "inn=${searchClientRequest.inn} " }
            return message
        }

        val ZONE_ID: ZoneId = ZoneId.of("UTC")
    }
}