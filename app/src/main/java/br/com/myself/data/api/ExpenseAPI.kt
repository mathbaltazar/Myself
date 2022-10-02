package br.com.myself.data.api

import br.com.myself.data.dto.RegistroDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface ExpenseAPI {

    @POST("expenses")
    suspend fun send(@Body registros: List<RegistroDTO>): Response<Void>

    @DELETE("expenses")
    suspend fun delete(@Body registros: List<RegistroDTO>): Response<Void>

}
