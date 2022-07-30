package br.com.myself.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.myself.data.BackendModelState
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class Entrada(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo val descricao: String,
    @ColumnInfo val data: Calendar,
    @ColumnInfo val valor: Double
) : BackendModelState(), Parcelable
