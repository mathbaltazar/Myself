package br.com.myself.domain.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.myself.domain.entity.Despesa

@Dao
interface DespesaDAO {
    
    @Query("SELECT * FROM Despesa ORDER BY id DESC")
    fun findAll(): LiveData<List<Despesa>>
    
    @Query("SELECT * FROM Despesa WHERE id =:id")
    fun find(id: Long): Despesa
    
    @Delete
    fun delete(despesa: Despesa)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persist(despesa: Despesa): Long
    
}
