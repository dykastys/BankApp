package ru.fillit.mdma.dm.persistence.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.transaction.annotation.EnableTransactionManagement
import ru.fillit.mdma.dm.audit.getAuthenticationIdentityFromContext
import java.util.*

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class JpaConfig {

    @Bean
    fun auditorProvider(): AuditorAware<String> = AuditorAware { Optional.of(getAuthenticationIdentityFromContext()) }
}