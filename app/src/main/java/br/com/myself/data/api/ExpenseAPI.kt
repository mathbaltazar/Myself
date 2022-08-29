package br.com.myself.data.api

import br.com.myself.data.dto.RegistroDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface ExpenseAPI {
    
    @POST("expense")
    suspend fun send(@Body registros: List<RegistroDTO>): Response<Void>
    
    @DELETE("registro/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Void>
}
