package br.com.myself.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.myself.model.entity.Registro

@Dao
interface RegistroDAO  {
    
    /** **Ordenados por Data e ID */
    @Query("SELECT * FROM Registro r ORDER BY r.data DESC, r.id DESC")
    fun findAllRegistros(): List<Registro>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persist(registro: Registro): Long
    
    @Query("DELETE FROM Registro WHERE id =:id")
    fun deleteById(id: Long)
    
    @Query("SELECT * FROM Registro WHERE data LIKE :mes AND data LIKE :ano ORDER BY data DESC, id DESC")
    fun findAllRegistrosByData(mes: String, ano: String): List<Registro>
    
    @Query("SELECT * FROM Registro WHERE despesa_id =:despesaId ORDER BY data DESC, id DESC")
    fun findAllRegistrosByDespesa(despesaId: Long): List<Registro>
    
    @Query("SELECT * FROM Registro WHERE descricao LIKE :buscar")
    fun findAllRegistrosByDescricao(buscar: String): List<Registro>
}
