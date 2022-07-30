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
data class Crise(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo var data: Calendar,
    @ColumnInfo var observacoes: String,
    @ColumnInfo var horario1: String,
    @ColumnInfo var horario2: String,
    //var arquivos ???
) : BackendModelState(), Parcelable