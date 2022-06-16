package br.com.myself.data.model

import androidx.room.*
import java.util.*

@Entity(
    /*foreignKeys = arrayOf(
        ForeignKey(
            entity = Despesa::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("despesa_id"),
            onDelete = ForeignKey.SET_NULL
        )
    )*/
)
data class Registro(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo val descricao: String,
    @ColumnInfo val valor: Double,
    @ColumnInfo val data: Calendar,
    @ColumnInfo val outros: String? = null,
    @ColumnInfo val despesa_id: Long? = null
)