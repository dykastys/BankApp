package ru.fillit.mdma.dm.errors

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ErrorHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException::class)
    fun handle(e: BadRequestException) = ErrorResponse(status = HttpStatus.BAD_REQUEST.value(), message = e.message)
        .also { LOG.error { "BadRequestException. Exception throw ${e.message}" } }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ClientsNotFound::class)
    fun handle(e: ClientsNotFound) = ErrorResponse(status = HttpStatus.NOT_FOUND.value(), message = e.message)
        .also { LOG.error { "NotFoundException. Exception throw ${e.message}" } }

    companion object {
        private val LOG = KotlinLogging.logger{ }
    }
}