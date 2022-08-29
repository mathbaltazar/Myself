package br.com.myself.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.myself.data.model.Despesa

@Dao
interface DespesaDAO {
    
    @Query("SELECT * FROM Despesa ORDER BY id DESC")
    fun findAll(): LiveData<List<Despesa>>
    
    @Delete
    fun delete(despesa: Despesa)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persist(despesa: Despesa): Long
    
}
