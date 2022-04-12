package br.com.myself.domain.dao

import androidx.room.*
import br.com.myself.domain.entity.Registro

@Dao
interface RegistroDAO  {
    
    /** **Ordenados por Data e ID */
    @Query("SELECT * FROM Registro WHERE data LIKE :mes AND data LIKE :ano ORDER BY data DESC, id DESC")
    fun findAllRegistrosByData(mes: String, ano: String): List<Registro>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun persist(registro: Registro): Long
    
    @Delete
    fun delete(registro: Registro)
    
    @Query("SELECT * FROM Registro WHERE despesa_id =:despesaId ORDER BY data DESC, id DESC")
    fun findAllRegistrosByDespesa(despesaId: Long): List<Registro>
    
    @Query("SELECT * FROM Registro WHERE descricao LIKE :buscar")
    fun findAllRegistrosByDescricao(buscar: String): List<Registro>
}
