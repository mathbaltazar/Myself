package br.com.myself.domain.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.myself.domain.dao.CriseDAO
import br.com.myself.domain.dao.DespesaDAO
import br.com.myself.domain.dao.EntradaDAO
import br.com.myself.domain.dao.RegistroDAO
import br.com.myself.domain.database.convertors.DateConverter
import br.com.myself.domain.entity.Crise
import br.com.myself.domain.entity.Despesa
import br.com.myself.domain.entity.Entrada
import br.com.myself.domain.entity.Registro

@Database(entities = arrayOf(
    Registro::class,
    Despesa::class,
    Entrada::class,
    Crise::class
), version = 3, exportSchema = true)
@TypeConverters(DateConverter::class)
abstract class MyDatabase : RoomDatabase() {
    
    abstract fun getRegistroDAO(): RegistroDAO
    
    abstract fun getDespesaDAO(): DespesaDAO
    
    abstract fun getEntradaDAO(): EntradaDAO
    
    abstract fun getCriseDAO(): CriseDAO
    
    companion object {
        const val NAME = "_myself-database"
    
        var instance: MyDatabase? = null
        fun getInstance(context: Context) = synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                MyDatabase::class.java,
                NAME
            ).fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
        
    }
}