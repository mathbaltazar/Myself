package br.com.myself.data.model

import android.os.Parcelable
import androidx.room.*
import br.com.myself.data.BackendModelState
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class Registro(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo val descricao: String,
    @ColumnInfo val valor: Double,
    @ColumnInfo val data: Calendar,
    @ColumnInfo val outros: String? = null
): BackendModelState(), Parcelable