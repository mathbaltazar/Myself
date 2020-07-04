package com.baltazarstudio.regular.service.dto

import com.baltazarstudio.regular.model.Configuracao
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.model.Movimento

class SincronizarDadosBackupDTO {

    var movimentos: List<Movimento>? = null
    var entradas: List<Entrada>? = null
    var configuracao: Configuracao? = null
}
