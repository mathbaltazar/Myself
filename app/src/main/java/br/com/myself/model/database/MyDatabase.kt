package br.com.myself.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.myself.model.dao.*
import br.com.myself.model.database.convertors.DateConverter
import br.com.myself.model.entity.*

@Database(entities = arrayOf(
    Registro::class,
    Despesa::class,
    Entrada::class,
    Crise::class
), version = 1, exportSchema = true)
@TypeConverters(DateConverter::class)
abstract class MyDatabase : RoomDatabase() {
    
    companion object { const val NAME = "_myself-database" }
    
    abstract fun getRegistroDAO(): RegistroDAO
    
    //abstract fun getDespesaDAO(): DespesaDAO
    
    //abstract fun getEntradaDAO(): EntradaDAO
    
    //abstract fun getCriseDAO(): CriseDAO
    
}