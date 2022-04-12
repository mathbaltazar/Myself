package br.com.myself.domain.dao

import androidx.room.*
import br.com.myself.domain.entity.Entrada

@Dao
interface EntradaDAO {
    
    @Query("SELECT * FROM Entrada WHERE data LIKE :monthLike AND data LIKE :yearLike ORDER BY data DESC, id DESC")
    fun findAllByMonth(monthLike: String, yearLike: String): List<Entrada>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persist(entrada: Entrada): Long
    
    @Delete
    fun delete (entrada: Entrada)
}