package ru.fillit.mdma.dm.web.api

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.fillit.mdma.dm.service.ClientService
import ru.fillit.mdma.dm.web.dto.ItemResponse
import ru.fillit.mdma.dm.web.dto.ItemsResponse
import ru.fillit.mdma.dm.web.dto.types.*

@RestController
@RequestMapping("/dm/client")
class ClientController(
    val clientService: ClientService
) {

    @Operation(description = "Получение клиентов и их данных")
    @PostMapping
    fun getClients(@RequestBody searchClientRequest: SearchClientRequest) : ItemsResponse<ClientResponse> =
        clientService.getClientsByClientRequest(searchClientRequest)

    @Operation(description = "Поиск всех контактов клиента")
    @PostMapping("/contact")
    fun getContacts(@RequestBody clientIdRequest: ClientIdRequest): ItemsResponse<ContactResponse> =
        clientService.findContactsByClientId(clientIdRequest)

    @Operation(description = "Поиск всех счетов клиента")
    @PostMapping("/account")
    fun getAccounts(@RequestBody clientIdRequest: ClientIdRequest): ItemsResponse<AccountResponse> =
        clientService.findAccountsByClientId(clientIdRequest)

    @Operation(description = "cумма текущего баланса запрошенного счета")
    @PostMapping("/account/balance")
    fun getAccountBalance(@RequestBody accountRequest: AccountRequest): ItemResponse<BalanceAmountResponse> =
        clientService.findBalanceForAccount(accountRequest)

    @Operation(description = "Поиск операций по счету. Сортировка операций - по убыванию даты операции")
    @PostMapping("/account/operation")
    fun getAccountOperations(@RequestBody accountRequest: AccountRequest): ItemsResponse<BalanceOperationResponse> =
        clientService.findOperationsForAccount(accountRequest)
}