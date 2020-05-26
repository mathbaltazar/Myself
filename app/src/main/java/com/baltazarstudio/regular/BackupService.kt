package com.baltazarstudio.regular

import com.baltazarstudio.regular.model.Movimento
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET

interface BackupService {

    @GET("/pessoas")
    fun restoreDataObservable(): Observable<List<Movimento>>

    @GET("/pessoas")
    fun restoreDataCall(): Call<List<Movimento>>
}
