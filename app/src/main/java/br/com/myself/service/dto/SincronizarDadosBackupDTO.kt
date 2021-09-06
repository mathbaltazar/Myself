package br.com.myself.service.dto

import br.com.myself.model.Backup
import br.com.myself.model.Despesa
import br.com.myself.model.Entrada
import br.com.myself.model.Registro

class SincronizarDadosBackupDTO {

    var registros: List<Registro>? = null
    var entradas: List<Entrada>? = null
    var despesas: List<Despesa>? = null
    var backup: Backup? = null
}
