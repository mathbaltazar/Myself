package br.com.myself.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Entrada(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo val descricao: String,
    @ColumnInfo val data: Calendar,
    @ColumnInfo val valor: Double
)
