package ru.fillit.mdma.dm.errors

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ErrorResponse(
    val status: Int,
    val message: String?,
    val data: MutableMap<String,Any> = mutableMapOf(),

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now()
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ValidationError(
    val field: String,
    val type: String,
    val message: String
)