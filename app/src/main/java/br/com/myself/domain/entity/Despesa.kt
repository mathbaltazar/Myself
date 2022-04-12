package br.com.myself.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Despesa(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo var nome: String,
    @ColumnInfo var valor: Double = 0.0,
    @ColumnInfo var diaVencimento: Int = 0
)