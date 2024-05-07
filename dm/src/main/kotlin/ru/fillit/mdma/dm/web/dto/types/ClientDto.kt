package ru.fillit.mdma.dm.web.dto.types

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@JsonNaming(SnakeCaseStrategy::class)
data class SearchClientRequest(
    @Schema(example = "3", description = "id клиента")
    val id: Long? = null,
    @Schema(example = "Иванов", description = "фамилия клиента")
    val lastName: String? = null,
    @Schema(example = "Иван", description = "имя клиента")
    val firstName: String? = null,
    @Schema(example = "Иванович", description = "отчество клиента")
    val patronymic: String? = null,
    @Schema(example = "2024-01-05", description = "дата (день рождения клиента) в формате YYYY-MM-DD")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val birthDate: Calendar? = null,
    @Schema(example = "1234 567890", description = "серия и номер паспорта клиента, разделены пробелом")
    val passport: String? = null,
    @Schema(example = "123456789012", description = "инн клиента")
    val inn: String? = null
) {
    fun isEmpty()  = (
                    id == null &&
                    firstName == null && lastName == null && patronymic == null &&
                    birthDate == null && passport == null && inn == null
            )

    fun passportNotValid(passport: List<String>?): Boolean = (passport?.size != 2 || passport[0].length != 4 || passport[1].length != 6)

    fun innNotValid(inn: String?): Boolean = inn?.length != 12
}


@JsonNaming(SnakeCaseStrategy::class)
data class ClientResponse(
    @Schema(example = "3", description = "id клиента")
    val id: String,
    @Schema(example = "Иванов", description = "фамилия клиента")
    val lastName: String,
    @Schema(example = "Иван", description = "имя клиента")
    val firstName: String,
    @Schema(example = "Иванович", description = "отчество клиента")
    val patronymic: String?,
    @Schema(example = "2024-01-05", description = "дата (день рождения клиента) в формате YYYY-MM-DD")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val birthDate: Date,
    @Schema(example = "1234", description = "серия паспорта клиента")
    val passportSeries: String,
    @Schema(example = "567890", description = "номер паспорта клиента")
    val passportNumber: String,
    @Schema(example = "123456789012", description = "инн клиента")
    val inn: String,
    @Schema(example = "г.Москва, переулок Вахитова, д.13", description = "адрес клиента")
    val address: String?
)

@JsonNaming(SnakeCaseStrategy::class)
data class ClientIdRequest(
    @Schema(example = "3", description = "id клиента")
    val clientId: String
)

@JsonNaming(SnakeCaseStrategy::class)
data class ContactResponse(
    @Schema(example = "5", description = "id контакта")
    val id: String,
    @Schema(example = "3", description = "id клиента")
    val clientId: String,
    @Schema(example = "PHONE", description = "тип контакта (PHONE, MAIL)")
    val type: String,
    @Schema(example = "+79135553333", description = "контакт (телефон или e-mail)")
    val value: String,
    @Schema(example = "4576", description = "сокращенное значение контакта")
    val shortcut: String
)

@JsonNaming(SnakeCaseStrategy::class)
data class AccountResponse(
    @Schema(example = "3", description = "id клиента")
    val number: String,
    @Schema(example = "12892159423184945195", description = "номер счёта")
    val clientId: String,
    @Schema(example = "PAYMENT", description = "тип счёта")
    val type: String,
    @Schema(example = "RUR", description = "валюта счета")
    val currency: String,
    @Schema(example = "ACTIVE", description = "состояние счета")
    val status: String,
    @Schema(example = "2024-01-20", description = "дата открытия счета")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val openDate: Date,
    @Schema(example = "2024-01-20", description = "дата закрытия счета")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val closeDate: Date?,
    @Schema(example = "15", description = "отсрочка платежа (дни) для счета овердрафт")
    val deferment: String?,
    @Schema(example = "5195", description = "сокращённый номер счета")
    val shortcut: String
)

@JsonNaming(SnakeCaseStrategy::class)
data class AccountRequest(
    @Schema(example = "40817810853110005823", description = "номер счёта")
    val accountNumber: String,
    @Schema(example = "3", description = "*используется для получения операций по счёту (количество операций)")
    val quantity: Int? = null
)

@JsonNaming(SnakeCaseStrategy::class)
data class BalanceAmountResponse(
    @Schema(example = "150480.20", description = "cумма текущего баланса счета")
    val balanceAmount: String
)

@JsonNaming(SnakeCaseStrategy::class)
data class BalanceOperationResponse(
    @Schema(example = "PAYMENT", description = "тип счёта")
    val type: String,
    @Schema(example = "40817810853110005823", description = "номер счёта")
    val accountNumber: String,
    @Schema(example = "2024-01-20 12:39:54", description = "дата операции")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    val operDate: Date,
    @Schema(example = "25000.16", description = "сумма операции")
    val amount: String,
    @Schema(example = "покупка автомобиля", description = "описание операции")
    val description: String
)