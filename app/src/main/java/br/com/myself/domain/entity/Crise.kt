package br.com.myself.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crise(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo var data: Calendar,
    @ColumnInfo var observacoes: String,
    @ColumnInfo var horario1: String,
    @ColumnInfo var horario2: String
    //var arquivos ???
)