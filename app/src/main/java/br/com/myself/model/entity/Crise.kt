package br.com.myself.model

class Crise {
    
    var id: Long? = null
    var data: Long? = null
    var observacoes: String? = null
    var horario1: String? = null
    var horario2: String? = null
    //var arquivos ???

    
    override fun toString(): String {
        return "Crise(id=$id, data=$data, observacoes=$observacoes, horario1=$horario1, horario2=$horario2)"
    }
}
