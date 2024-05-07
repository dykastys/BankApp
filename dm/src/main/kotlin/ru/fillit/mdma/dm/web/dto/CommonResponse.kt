package ru.fillit.mdma.dm.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class ItemResponse<T>(val item: T)

fun <T> T.toItemResponse(): ItemResponse<T> = ItemResponse(this)

data class ItemsResponse<T>(
    @Schema(description = "Список вложенных элементов")
    val items: List<T>,
    @Schema(description = "Мета дата")
    val metadata: MetaData
) {
    constructor(items: List<T>) :
            this(
                items = items,
                metadata = MetaData(totalCount = items.size.toLong())
            )

    companion object {
        fun <T> empty(): ItemsResponse<T> = ItemsResponse(
            emptyList(),
            MetaData(0)
        )
    }
}

data class MetaData(
    @Schema(description = "Общее количество элементов", example = "148")
    @JsonProperty("total_count") val totalCount: Long
)

fun <T> List<T>.toItemsResponse(): ItemsResponse<T> = ItemsResponse(this)

fun <T> T.toList(): List<T> = listOf(this)

// TODO: 08.01.2024 добавить возможность постраничного получения данных