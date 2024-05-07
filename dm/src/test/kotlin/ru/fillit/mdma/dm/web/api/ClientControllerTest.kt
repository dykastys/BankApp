package ru.fillit.mdma.dm.web.api

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.post
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import ru.fillit.mdma.dm.persistence.entity.*
import ru.fillit.mdma.dm.persistence.entity.Currency
import ru.fillit.mdma.dm.persistence.entity.repository.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

@ActiveProfiles("test")
@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ClientControllerTest {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var clientRepository: ClientRepository

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var accountBalanceRepository: AccountBalanceRepository

    @Autowired
    private lateinit var operationRepository: OperationRepository

    @Autowired
    private lateinit var contactRepository: ContactRepository

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setupBeforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        accountBalanceRepository.deleteAll()
        operationRepository.deleteAll()
        accountRepository.deleteAll()
        contactRepository.deleteAll()
        clientRepository.deleteAll()
    }

    @Test
    fun `should returns one client`() {
        val birthday = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd").format(birthday.time)

        generateOneClient(birthday).also { clientRepository.save(it) }

        mockMvc.post("/dm/client") {
            contentType = MediaType.APPLICATION_JSON
            content =  """
                {
                    "first_name" : "Владислав",
                    "last_name" : "Анисипов",
                    "patronymic" : "Федорович",
                    "birthDate" : "$dateFormat",
                    "passport" : "2221 432234",
                    "inn" : "210987654321"
                }
            """.trimIndent()
        }
            .andDo { print() }
            .andExpect { status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.items[0].first_name", `is`("Владислав"))
                jsonPath("$.items[0].last_name", `is`("Анисипов"))
                jsonPath("$.items[0].patronymic", `is`("Федорович"))
                jsonPath("$.items[0].birth_date", `is`(dateFormat))
                jsonPath("$.items[0].passport_series", `is`("2221"))
                jsonPath("$.items[0].passport_number", `is`("432234"))
                jsonPath("$.items[0].inn", `is`("210987654321"))
            }
    }

    @Test
    fun `should returns 404 not found`() {
        val birthday = Calendar.getInstance()

        generateOneClient(birthday).also { clientRepository.save(it) }

        mockMvc.post("/dm/client") {
            contentType = MediaType.APPLICATION_JSON
            content =  """
                {
                    "first_name" : "Антон"
                }
            """.trimIndent()
        }
            .andDo { print() }
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `should returns several clients`() {
        val birthday = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd").format(birthday.time)

        generateTwoClients(birthday).also { clientRepository.saveAll(it) }

        mockMvc.post("/dm/client") {
            contentType = MediaType.APPLICATION_JSON
            content =  """
                {
                    "last_name" : "Анисипов"
                }
            """.trimIndent()
        }
            .andDo { print() }
            .andExpect { status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.items[0].first_name", `is`("Владислав"))
                jsonPath("$.items[0].last_name", `is`("Анисипов"))
                jsonPath("$.items[0].patronymic", `is`("Федорович"))
                jsonPath("$.items[0].birth_date", `is`(dateFormat))
                jsonPath("$.items[0].passport_series", `is`("2221"))
                jsonPath("$.items[0].passport_number", `is`("432234"))
                jsonPath("$.items[0].inn", `is`("210987654321"))
                jsonPath("$.items[1].first_name", `is`("Василий"))
                jsonPath("$.items[1].last_name", `is`("Анисипов"))
                jsonPath("$.items[1].patronymic", `is`("Михайлович"))
                jsonPath("$.items[1].birth_date", `is`(dateFormat))
                jsonPath("$.items[1].passport_series", `is`("1123"))
                jsonPath("$.items[1].passport_number", `is`("258369"))
                jsonPath("$.items[1].inn", `is`("524893025475"))
            }
    }

    @Test
    fun `should returns bad request`() {
        val birthday = Calendar.getInstance()

        generateOneClient(birthday).also { clientRepository.save(it) }

        mockMvc.post("/dm/client") {
            contentType = MediaType.APPLICATION_JSON
            content =  """
                {
                    "passport" : "2221 4322343",
                }
            """.trimIndent()
        }
            .andDo { print() }
            .andExpect { status { isBadRequest() } }

        mockMvc.post("/dm/client") {
            contentType = MediaType.APPLICATION_JSON
            content =  """
                {
                    "inn" : "2109876543217"
                }
            """.trimIndent()
        }
            .andDo { print() }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    fun `should returns client contacts`() {
        val birthday = Calendar.getInstance()

        val client = generateOneClient(birthday)
        val contacts = generateContactsForClient(client)

        client.contacts = contacts

        clientRepository.save(client)
        contactRepository.saveAll(contacts)

        mockMvc.post("/dm/client/contact") {
            contentType = MediaType.APPLICATION_JSON
            content =  """
                {
                    "client_id" : ${client.id}
                }
            """.trimIndent()
        }
            .andDo { print() }
            .andExpect { status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.items[0].type", `is`("PHONE"))
                jsonPath("$.items[0].value", `is`("8-999-999-99-99"))
                jsonPath("$.items[0].shortcut", `is`("9-99"))
                jsonPath("$.items[1].type", `is`("EMAIL"))
                jsonPath("$.items[1].value", `is`("aaa@aaa.com"))
                jsonPath("$.items[1].shortcut", `is`("a@aaa.com"))
            }
    }

    @Test
    fun `should returns client accounts`() {
        val birthday = Calendar.getInstance()

        val client = generateOneClient(birthday)

        val date = Calendar.getInstance().timeInMillis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val accounts = generateAccountsForClient(client, date)

        client.accounts = accounts

        clientRepository.save(client)
        accountRepository.saveAll(accounts)

        mockMvc.post("/dm/client/account") {
            contentType = MediaType.APPLICATION_JSON
            content =  """
                {
                    "client_id" : ${client.id}
                }
            """.trimIndent()
        }
            .andDo { print() }
            .andExpect { status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.items[0].number", `is`("12345678901234567890"))
                jsonPath("$.items[0].type", `is`("PAYMENT"))
                jsonPath("$.items[0].currency", `is`("RUR"))
                jsonPath("$.items[0].status", `is`("ACTIVE"))
                jsonPath("$.items[0].open_date", `is`(dateFormat.format(Date(date))))
                jsonPath("$.items[0].close_date", nullValue())
                jsonPath("$.items[0].shortcut", `is`("2345"))
                jsonPath("$.items[1].number", `is`("54321"))
                jsonPath("$.items[1].type", `is`("BUDGET"))
                jsonPath("$.items[1].currency", `is`("RUR"))
                jsonPath("$.items[1].status", `is`("CLOSED"))
                jsonPath("$.items[1].open_date", `is`(dateFormat.format(Date(date - 1000000L))))
                jsonPath("$.items[1].close_date", `is`(dateFormat.format(Date(date))))
                jsonPath("$.items[1].shortcut", `is`("4321"))
            }
    }

    @Test
    fun `should returns client account balance`() {
        val birthday = Calendar.getInstance()
        val client = generateOneClient(birthday)

        val date = Calendar.getInstance().timeInMillis
        val account = generateOneAccountForClient(client, date)

        val amount = BigDecimal("123456.6543")
        val accountBalance = generateAccountBalance(account, date, amount)

        client.accounts = setOf(account)

        clientRepository.save(client)
        accountRepository.save(account)
        accountBalanceRepository.save(accountBalance)

        mockMvc.post("/dm/client/account/balance") {
            contentType = MediaType.APPLICATION_JSON
            content =  """
                {
                    "account_number" : "${account.number}"
                }
            """.trimIndent()
        }
            .andDo { print() }
            .andExpect { status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.item.balance_amount", `is`(amount.toString()))
            }
    }

    @Test
    fun `should returns client account operations`() {
        val birthday = Calendar.getInstance()
        val client = generateOneClient(birthday)

        val date = Calendar.getInstance().timeInMillis
        val account = generateOneAccountForClient(client, date)

        client.accounts = setOf(account)

        val amount1 = BigDecimal("12345.675")
        val amount2 = BigDecimal("333.333")
        val operations = generateTwoOperations(account, date, amount1, amount2)

        clientRepository.save(client)
        accountRepository.save(account)
        operationRepository.saveAll(operations)

        mockMvc.post("/dm/client/account/operation") {
            contentType = MediaType.APPLICATION_JSON
            content =  """
                {
                    "account_number" : "${account.number}",
                    "quantity" : "2"
                }
            """.trimIndent()
        }
            .andDo { print() }
            .andExpect { status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
    }

    private fun generateOneClient(birthday: Calendar) =
        Client(
            id = null,
            accounts = null,
            contacts = HashSet(),
            firstName = "Владислав",
            lastName = "Анисипов",
            patronymic = "Федорович",
            birthDate = birthday.timeInMillis,
            passportSeries = "2221",
            passportNumber = "432234",
            inn = "210987654321"
        )

    private fun generateTwoClients(birthday: Calendar) = listOf(
        generateOneClient(birthday),
        Client(
            id = null,
            accounts = null,
            contacts = HashSet(),
            firstName = "Владислав",
            lastName = "Анисипов",
            patronymic = "Федорович",
            birthDate = birthday.timeInMillis,
            passportSeries = "2221",
            passportNumber = "432234",
            inn = "210987654321"
        )
    )

    private fun generateContactsForClient(client: Client) = setOf(
        Contact(
            id = 1L,
            client = client,
            type = ContactType.PHONE,
            value = "8-999-999-99-99"
        ),
        Contact(
            id = 2L,
            client = client,
            type = ContactType.EMAIL,
            value = "aaa@aaa.com"
        )
    )

    private fun generateOneAccountForClient(client: Client, date: Long) =
        Account(
            number = "12345678901234567890",
            client = client,
            operations = listOf(),
            type = AccountType.PAYMENT,
            currency = Currency.RUR,
            status = AccountStatus.ACTIVE,
            openDate = date - 1000000L
        )

    private fun generateAccountsForClient(client: Client, date: Long) = setOf(
        generateOneAccountForClient(client, date),
        Account(
            number = "54321",
            client = client,
            operations = listOf(),
            type = AccountType.BUDGET,
            currency = Currency.RUR,
            status = AccountStatus.CLOSED,
            openDate = date - 1000000L,
            closeDate = date
        )
    )

    private fun generateAccountBalance(account: Account, date: Long, amount: BigDecimal) =
        AccountBalance(
            accountNumber = account.number,
            account = account,
            balanceDate = date,
            amount = amount
        )

    private fun generateTwoOperations(account: Account, date: Long, amount1: BigDecimal, amount2: BigDecimal) = setOf(
        Operation(
            id = 1L,
            type = OperationType.RECEIPT,
            account = account,
            operDate = date - 10000000,
            amount = amount1,
            description = "one"
        ),
        Operation(
            id = 2L,
            type = OperationType.EXPENSE,
            account = account,
            operDate = date - 1000000,
            amount = amount2,
            description = "two"
        )
    )
}