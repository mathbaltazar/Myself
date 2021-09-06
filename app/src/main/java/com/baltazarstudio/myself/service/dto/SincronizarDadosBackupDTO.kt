package com.baltazarstudio.myself.service.dto

import com.baltazarstudio.myself.model.Backup
import com.baltazarstudio.myself.model.Despesa
import com.baltazarstudio.myself.model.Entrada
import com.baltazarstudio.myself.model.Registro

class SincronizarDadosBackupDTO {

    var registros: List<Registro>? = null
    var entradas: List<Entrada>? = null
    var despesas: List<Despesa>? = null
    var backup: Backup? = null
}
