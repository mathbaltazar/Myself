package br.com.myself.service.dto

import br.com.myself.model.entity.Backup
import br.com.myself.model.entity.Despesa
import br.com.myself.model.entity.Entrada
import br.com.myself.model.entity.Registro

class SincronizarDadosBackupDTO {

    var registros: List<Registro>? = null
    var entradas: List<Entrada>? = null
    var despesas: List<Despesa>? = null
    var backup: Backup? = null
}
