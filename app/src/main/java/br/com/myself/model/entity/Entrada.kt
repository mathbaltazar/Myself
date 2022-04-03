package br.com.myself.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Entrada(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo var descricao: String,
    @ColumnInfo var data: Calendar,
    @ColumnInfo var valor: Double = 0.0
)
