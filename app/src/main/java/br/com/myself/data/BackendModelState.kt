package br.com.myself.data

import androidx.room.ColumnInfo

open  class BackendModelState {
    @ColumnInfo(name = "serverId")
    var serverId: Long? = null
    @ColumnInfo(name = "synchronized")
    var isSynchronized: Boolean = false
    @ColumnInfo(name = "deleted")
    var isDeleted: Boolean = false
}