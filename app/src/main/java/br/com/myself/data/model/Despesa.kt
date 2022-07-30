package br.com.myself.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.myself.data.BackendModelState
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Despesa(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo var nome: String,
    @ColumnInfo var valor: Double = 0.0,
    @ColumnInfo var diaVencimento: Int = 0,
) : BackendModelState(), Parcelable