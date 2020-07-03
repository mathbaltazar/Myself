package com.baltazarstudio.regular.service.dto

import com.baltazarstudio.regular.model.Configuracao
import com.baltazarstudio.regular.model.Movimento

class SincronizarDadosBackupDTO {

    var movimentos: List<Movimento>? = null
    var configuracao: Configuracao? = null
}
