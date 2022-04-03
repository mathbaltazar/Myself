package br.com.myself.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Crise {
    
    @PrimaryKey(autoGenerate = true) var id: Long? = null
    @ColumnInfo var data: Long? = null
    @ColumnInfo var observacoes: String? = null
    @ColumnInfo var horario1: String? = null
    @ColumnInfo var horario2: String? = null
    //var arquivos ???

    
    override fun toString(): String {
        return "Crise(id=$id, data=$data, observacoes=$observacoes, horario1=$horario1, horario2=$horario2)"
    }
}
