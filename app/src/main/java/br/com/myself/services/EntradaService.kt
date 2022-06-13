package br.com.myself.services

import br.com.myself.data.dto.EntradaDTO
import br.com.myself.data.dto.PageResultEntrada
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface EntradaService {
    
    @GET("entradas/{year}")
    fun getEntradas(
        @Path("year") ano: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Observable<Response<PageResultEntrada>>
    
    @DELETE("entradas/{id}")
    suspend fun deleteById(@Path("id") id: Long): Response<Void>
    
    @POST("entradas")
    suspend fun insertOrUpdate(@Body entrada: EntradaDTO): Response<EntradaDTO>
    
}