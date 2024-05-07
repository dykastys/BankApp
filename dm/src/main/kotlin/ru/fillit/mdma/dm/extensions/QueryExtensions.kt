package ru.fillit.mdma.dm.extensions

import jakarta.persistence.criteria.Path
import kotlin.reflect.KProperty1

fun <R> Path<*>.get(prop: KProperty1<*, R?>): Path<R> = this.get(prop.name)