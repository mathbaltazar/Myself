package br.com.myself.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import br.com.myself.data.model.Entrada
import kotlinx.coroutines.flow.Flow

@Dao
interface EntradaDAO {
    
    @Query("SELECT * FROM Entrada WHERE data LIKE :yearLike AND deleted =:deleted ORDER BY data DESC, id DESC")
    fun findAllByYear(yearLike: String, deleted: Boolean = false): PagingSource<Int, Entrada>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun persist(entrada: Entrada): Long
    
    @Delete
    suspend fun delete (entrada: Entrada)
    
    @Query("SELECT COUNT(*) FROM Entrada WHERE data LIKE :yearLike")
    fun countByYear(yearLike: String): LiveData<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entradas: List<Entrada>)
    
    @Query("DELETE FROM Entrada")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM Entrada WHERE id=:itemId")
    fun findById(itemId: Long): LiveData<Entrada>
    
    @Query("SELECT * FROM Entrada WHERE synchronized =:sync")
    fun findAllToSync(sync: Boolean = false): Flow<List<Entrada>>
}