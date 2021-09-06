package com.baltazarstudio.myself.service

import com.baltazarstudio.myself.service.dto.SincronizarDadosBackupDTO
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BackupService {

    @POST("/restaurar")
    fun restaurarDados(): Observable<Response<SincronizarDadosBackupDTO>>

    @POST("/sincronizarDados")
    fun sincronizarDados(@Body request: SincronizarDadosBackupDTO): Observable<Response<Void>>

}
