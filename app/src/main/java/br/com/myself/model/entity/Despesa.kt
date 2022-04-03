package br.com.myself.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Despesa {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    @ColumnInfo var nome: String? = null
    @ColumnInfo var valor: Double = 0.0
    @ColumnInfo var diaVencimento: Int = 0
    
}