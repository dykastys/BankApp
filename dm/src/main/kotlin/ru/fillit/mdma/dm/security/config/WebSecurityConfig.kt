package ru.fillit.mdma.dm.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
class WebSecurityConfiguration {

    @Bean
    fun filterChainBasic(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeRequests {
                authorize("/**", permitAll)
                authorize(anyRequest, authenticated)
            }
        }
        return http.build()
    }
}