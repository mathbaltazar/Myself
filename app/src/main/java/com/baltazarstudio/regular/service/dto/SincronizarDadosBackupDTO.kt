package com.baltazarstudio.regular.service.dto

import com.baltazarstudio.regular.model.Backup
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.model.Registro

class SincronizarDadosBackupDTO {

    var registros: List<Registro>? = null
    var entradas: List<Entrada>? = null
    var despesas: List<Despesa>? = null
    var backup: Backup? = null
}
