package br.com.myself.domain.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import br.com.myself.domain.entity.Entrada

@Dao
interface EntradaDAO {
    
    @Query("SELECT * FROM Entrada WHERE data LIKE :yearLike ORDER BY data DESC")
    fun findAllByYear(yearLike: String): PagingSource<Int, Entrada>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persist(entrada: Entrada): Long
    
    @Delete
    fun delete (entrada: Entrada)
    
    @Query("SELECT COUNT(*) FROM Entrada WHERE data LIKE :yearLike")
    fun countByYear(yearLike: String): LiveData<Int>
}