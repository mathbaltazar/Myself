package br.com.myself.data.api

import java.io.IOException

private const val TIMEOUT = "Timeout...verificar conexão com o servidor"
private const val NOT_SUCCESSFUL = "Not successful...falha no operação, verificar dados"
private const val SERVER_NOT_SUCCESSFUL = "Server Not successful...olhar código servidor"
private const val UNKNOWN = "Unknown...desconhecido, olhar código"

sealed class BackendError(message: String): IOException(message) {
    class TimeOut : BackendError(TIMEOUT)
    class Unknown : BackendError(UNKNOWN)
    class ServerNotSuccessful : BackendError(SERVER_NOT_SUCCESSFUL)
    class NotSuccessful : BackendError(NOT_SUCCESSFUL)
}