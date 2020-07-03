package com.baltazarstudio.regular.service

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.POST

interface ConnectionTestService {

    @POST("/testeConexao")
    fun test(): Observable<Response<Void>>
}