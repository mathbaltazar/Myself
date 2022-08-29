package br.com.myself.data

import androidx.room.ColumnInfo
import java.util.*

open class BackendModelState {
    @ColumnInfo(name = "object_id")
    var objectID: String = UUID.randomUUID().toString()
    @ColumnInfo(name = "synchronized")
    var isSynchronized: Boolean = false
    @ColumnInfo(name = "deleted")
    var isDeleted: Boolean = false
}