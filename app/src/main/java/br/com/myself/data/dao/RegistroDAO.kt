package br.com.myself.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.myself.data.model.Registro
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDAO {
    
    @Query("SELECT * FROM Registro WHERE data LIKE :mes AND data LIKE :ano AND deleted = 0 ORDER BY data DESC, id DESC")
    fun findAllRegistrosByData(mes: String, ano: String): LiveData<List<Registro>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun persist(registro: Registro): Long
    
    @Delete
    suspend fun delete(registro: Registro)
    
    @Query("SELECT DISTINCT(valor) FROM Registro WHERE despesa_id =:despesaId")
    fun findAllValorByDespesaId(despesaId: Long): List<Double>
    
    @Query("SELECT * FROM Registro WHERE despesa_id =:despesaId ORDER BY data DESC, id DESC")
    fun findAllRegistrosByDespesaId(despesaId: Long): LiveData<List<Registro>>
    
    @Query("SELECT * FROM Registro WHERE descricao LIKE :buscar")
    fun findAllRegistrosByDescricao(buscar: String): LiveData<List<Registro>>
    
    @Query("SELECT * FROM Registro WHERE id =:registroId")
    fun findById(registroId: Long): LiveData<Registro>
    
    @Query("SELECT * FROM Registro WHERE synchronized = 0 OR deleted = 1")
    fun findAllToSync(): LiveData<List<Registro>>
    
    @Delete
    suspend fun delete(registro: Array<Registro>)
    
    @Update
    suspend fun persist(registro: Array<Registro>)
}
