package br.com.myself.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.myself.data.dao.CriseDAO
import br.com.myself.data.dao.DespesaDAO
import br.com.myself.data.dao.EntradaDAO
import br.com.myself.data.dao.RegistroDAO
import br.com.myself.database.convertors.DateConverter
import br.com.myself.data.model.Crise
import br.com.myself.data.model.Despesa
import br.com.myself.data.model.Entrada
import br.com.myself.data.model.Registro

@Database(entities = [
    Registro::class,
    Despesa::class,
    Entrada::class,
    Crise::class
],
version = 1, exportSchema = true)
@TypeConverters(DateConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    
    abstract fun getRegistroDAO(): RegistroDAO
    
    abstract fun getDespesaDAO(): DespesaDAO
    
    abstract fun getEntradaDAO(): EntradaDAO
    
    abstract fun getCriseDAO(): CriseDAO
    
    companion object {
        private const val DATABASE_NAME = "_myself"
        var instance: LocalDatabase? = null
        fun getInstance(context: Context) = synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                LocalDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
    }
}