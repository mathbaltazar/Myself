package br.com.myself.domain.dao

import androidx.room.*
import br.com.myself.domain.entity.Crise

@Dao
interface CriseDAO {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persist(crise: Crise): Long
    
    @Delete
    fun delete(crise: Crise)
    
    @Query("SELECT * FROM Crise ORDER BY data DESC")
    fun findAll(): List<Crise>

}
