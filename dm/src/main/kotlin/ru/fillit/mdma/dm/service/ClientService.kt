package ru.fillit.mdma.dm.service

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import org.hibernate.query.Query
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.fillit.mdma.dm.errors.BadRequestException
import ru.fillit.mdma.dm.errors.ClientsNotFound
import ru.fillit.mdma.dm.extensions.Handler
import ru.fillit.mdma.dm.extensions.get
import ru.fillit.mdma.dm.persistence.entity.Account
import ru.fillit.mdma.dm.persistence.entity.Client
import ru.fillit.mdma.dm.persistence.entity.Operation
import ru.fillit.mdma.dm.persistence.entity.repository.AccountBalanceRepository
import ru.fillit.mdma.dm.persistence.entity.repository.AccountRepository
import ru.fillit.mdma.dm.persistence.entity.repository.ContactRepository
import ru.fillit.mdma.dm.web.dto.ItemResponse
import ru.fillit.mdma.dm.web.dto.ItemsResponse
import ru.fillit.mdma.dm.web.dto.toItemResponse
import ru.fillit.mdma.dm.web.dto.toItemsResponse
import ru.fillit.mdma.dm.web.dto.types.*

@Service
class ClientService(
    private val entityManager: EntityManager,
    private val contactRepository: ContactRepository,
    private val accountRepository: AccountRepository,
    private val accountBalanceRepository: AccountBalanceRepository
) {

    private val criteriaBuilder: CriteriaBuilder = entityManager.criteriaBuilder

    @Transactional
    fun getClientsByClientRequest(searchClientRequest: SearchClientRequest): ItemsResponse<ClientResponse> {
        if (searchClientRequest.isEmpty()) {
            throw BadRequestException("all clientRequest parameters are empty")
        }
        val tg = entityManager.createQuery(
            makeCriteria(criteriaBuilder.createQuery(Client::class.java), searchClientRequest)
        )
        val clients = tg.resultList
        if (clients.isEmpty()) {
            throw ClientsNotFound("Clients are not found by parameters: ${Handler.getMessageByParameters(searchClientRequest)}")
        }
        return clients.map { it.toClientResponse() }.toItemsResponse()
    }

    private fun <T> makeCriteria(
        query: CriteriaQuery<T>,
        request: SearchClientRequest
    ): CriteriaQuery<T> {
        val root = query.from(Client::class.java)
        val predicates = mutableListOf<Predicate>()

        if (request.id != null) {
            predicates += criteriaBuilder.equal(root.get(Client::id), request.id)
        }
        if (request.firstName != null) {
            predicates += criteriaBuilder.equal(root.get(Client::firstName), request.firstName)
        }
        if (request.lastName != null) {
            predicates += criteriaBuilder.equal(root.get(Client::lastName), request.lastName)
        }
        if (request.patronymic != null) {
            predicates += criteriaBuilder.equal(root.get(Client::patronymic), request.patronymic)
        }
        if (request.birthDate != null) {
            predicates += criteriaBuilder.equal(root.get(Client::birthDate), request.birthDate.time)
        }
        if (request.passport != null) {
            val passportArr = request.passport.split(" ")
            if (request.passportNotValid(passportArr)) {
                throw BadRequestException("Passport=${request.passport} is not valid")
            }
            predicates += criteriaBuilder.equal(root.get(Client::passportSeries), passportArr[0])
            predicates += criteriaBuilder.equal(root.get(Client::passportNumber), passportArr[1])
        }
        if (request.inn != null) {
            if (request.innNotValid(request.inn)) {
                throw BadRequestException("Inn=${request.inn} is not valid")
            }
            predicates += criteriaBuilder.equal(root.get(Client::inn), request.inn)
        }
        return query.where(*predicates.toTypedArray())
    }

    @Transactional
    fun findContactsByClientId(clientIdRequest: ClientIdRequest): ItemsResponse<ContactResponse> {
        val clientId = getLongClientIdFromString(clientIdRequest.clientId)
        val contacts = contactRepository.findContactsByClientId(clientId)
        return contacts.map { it.toContactResponse() }.toItemsResponse()
    }

    @Transactional
    fun findAccountsByClientId(clientIdRequest: ClientIdRequest): ItemsResponse<AccountResponse> {
        val clientId = getLongClientIdFromString(clientIdRequest.clientId)
        val accounts = accountRepository.findAccountsByClientId(clientId)
        return accounts.map { it.toAccountResponse() }.toItemsResponse()
    }

    private fun getLongClientIdFromString(clientIdStr: String): Long =
        try {
            clientIdStr.toLong()
        } catch (e: java.lang.NumberFormatException) {
            throw BadRequestException("ClientId='${clientIdStr}' is not a number")
        }

    @Transactional
    fun findBalanceForAccount(accountRequest: AccountRequest): ItemResponse<BalanceAmountResponse> {
        if(accountRequest.accountNumber.length != 20) {
            throw BadRequestException("Account number='${accountRequest.accountNumber}' is not valid")
        }
        val accountBalance = accountBalanceRepository.findAccountBalanceByAccountNumber(accountRequest.accountNumber)
        return BalanceAmountResponse(accountBalance).toItemResponse()
    }

    @Transactional
    fun findOperationsForAccount(accountRequest: AccountRequest): ItemsResponse<BalanceOperationResponse> {
        if(accountRequest.accountNumber.length != 20) {
            throw BadRequestException("Account number='${accountRequest.accountNumber}' is not valid")
        }
        if(accountRequest.quantity == null) {
            throw BadRequestException("Quantity in request is must be null")
        }
        val tg = entityManager.createQuery(
            makeCriteria(criteriaBuilder.createQuery(Operation::class.java), accountRequest)
        )
        tg.maxResults = accountRequest.quantity
        return tg.resultList.map { it.toBalanceOperationResponse() }.toItemsResponse()
    }

    private fun getOperationsByAccountNumberWithLimit(accountRequest: AccountRequest): List<Operation> {
        val hql = "select o from Operation o " +
                "join fetch o.account a " +
                "join fetch a.client c " +
                "where a.number = :accountNumber " +
                "ORDER BY o.operDate desc"
        val query: Query<Operation> = entityManager.createQuery(hql, Operation::class.java) as Query<Operation>
        query.setParameter("accountNumber", accountRequest.accountNumber)
        query.maxResults = accountRequest.quantity!!
        return query.resultList
    }

    private fun <T> makeCriteria(
        query: CriteriaQuery<T>,
        request: AccountRequest
    ): CriteriaQuery<T> {
        val root = query.from(Operation::class.java)

        val predicates = mutableListOf<Predicate>()

        predicates += criteriaBuilder.equal(root.get(Operation::account).get(Account::number), request.accountNumber)

        return query.where(*predicates.toTypedArray())
    }
}