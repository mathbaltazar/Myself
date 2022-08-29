package br.com.myself.data.api

import br.com.myself.data.dto.EntradaDTO
import retrofit2.Response
import retrofit2.http.*

interface EntradaAPI {
    
    @GET("entradas/{year}")
    fun getEntradas(): Response<List<EntradaDTO>> // TODO Refatorar endpoint no backend
    
    @DELETE("entradas/{id}")
    suspend fun deleteById(@Path("id") id: String): Response<Void>
    
    @POST("entradas")
    suspend fun insertOrUpdate(@Body entrada: EntradaDTO): Response<EntradaDTO>
    
}