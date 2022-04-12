package br.com.myself.services.dto

import br.com.myself.domain.entity.Backup
import br.com.myself.domain.entity.Despesa
import br.com.myself.domain.entity.Entrada
import br.com.myself.domain.entity.Registro

class SincronizarDadosBackupDTO {

    var registros: List<Registro>? = null
    var entradas: List<Entrada>? = null
    var despesas: List<Despesa>? = null
    var backup: Backup? = null
}
