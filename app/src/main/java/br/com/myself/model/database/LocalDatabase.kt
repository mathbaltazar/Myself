package br.com.myself.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.myself.model.dao.CriseDAO
import br.com.myself.model.dao.DespesaDAO
import br.com.myself.model.dao.EntradaDAO
import br.com.myself.model.dao.RegistroDAO
import br.com.myself.model.database.convertors.DateConverter
import br.com.myself.model.entity.Crise
import br.com.myself.model.entity.Despesa
import br.com.myself.model.entity.Entrada
import br.com.myself.model.entity.Registro

@Database(entities = arrayOf(
    Registro::class,
    Despesa::class,
    Entrada::class,
    Crise::class
), version = 3, exportSchema = true)
@TypeConverters(DateConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    
    abstract fun getRegistroDAO(): RegistroDAO
    
    abstract fun getDespesaDAO(): DespesaDAO
    
    abstract fun getEntradaDAO(): EntradaDAO
    
    abstract fun getCriseDAO(): CriseDAO
    
    companion object {
        const val NAME = "_myself-database"
    
        var instance: LocalDatabase? = null
        fun getInstance(context: Context) = synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                LocalDatabase::class.java,
                NAME
            ).fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
        
    }
}