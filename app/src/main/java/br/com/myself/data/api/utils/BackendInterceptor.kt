package br.com.myself.data.api.utils

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.logging.Level
import java.util.logging.Logger

class BackendInterceptor : Interceptor {
    
    companion object {
        private val logger: Logger =
            synchronized(this) {
                Logger.getLogger(BackendInterceptor::class.java.name)
                    .apply { level = Level.FINER }
            }
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        logRequest(request)
        
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: IOException) {
            e.printStackTrace()
    
            throw (if (e is SocketTimeoutException) BackendError.TimeOut()
            else BackendError.Unknown())
                .also { logError(it) }
        }
    
        if (!response.isSuccessful) {
            throw (if (response.code() >= 500) BackendError.ServerNotSuccessful()
            else BackendError.NotSuccessful())
                .also { logError(it) }
        }
        
        logResponse(response)
        return response
    }
    
    private fun logError(e: BackendError) {
        logger.log(Level.WARNING, "----> ERROR OF REQUEST")
        logger.log(Level.WARNING, "Type: ${e::class.java.name}")
        logger.log(Level.WARNING, "Cause: ${e::message}")
        logger.log(Level.WARNING, "<---- END ERROR OF REQUEST")
    }
    
    private fun logRequest(request: Request) {
        val logStringMethod = "Method: ${request.method()}"
        val logStringUrl = "URL: ${request.url()}"
        val logStringBody = "Body: ${request.body() ?: "Empty body"}"
        
        logger.log(Level.FINE, "----> SENDING REQUEST TO BACKEND")
        logger.log(Level.FINE, logStringMethod)
        logger.log(Level.FINE, logStringUrl)
        logger.log(Level.FINE, logStringBody)
        logger.log(Level.FINE, "<---- END OF REQUEST TO BACKEND")
    }
    
    private fun logResponse(response: Response) {
        val logStringCode = "Code: ${response.code()}"
        val logStringMessage = "Message: ${response.message()}"
        val logStringBody = "Body: ${response.body()}"
        
        logger.log(Level.FINE, "----> RETURNED RESPONSE FROM BACKEND")
        logger.log(Level.FINE, logStringCode)
        logger.log(Level.FINE, logStringMessage)
        logger.log(Level.FINE, logStringBody)
        logger.log(Level.FINE, "<---- END OF RESPONSE TO BACKEND")
    }
    
}