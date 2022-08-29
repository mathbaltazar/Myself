package br.com.myself.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.myself.data.model.Registro

@Dao
interface RegistroDAO {
    
    @Query("SELECT * FROM Registro WHERE data LIKE :mes AND data LIKE :ano AND deleted = 0 ORDER BY data DESC, id DESC")
    fun findAllRegistrosByData(mes: String, ano: String): LiveData<List<Registro>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun persist(registro: Registro): Long

    @Query("SELECT * FROM Registro WHERE descricao LIKE :buscar")
    fun findAllRegistrosByDescricao(buscar: String): LiveData<List<Registro>>
    
    @Query("SELECT * FROM Registro WHERE id =:registroId")
    fun findById(registroId: Long): LiveData<Registro>
    
    @Query("SELECT * FROM Registro WHERE synchronized = 0 OR deleted = 1")
    fun findAllToSync(): LiveData<List<Registro>>
    
    @Update
    suspend fun persist(registro: Array<Registro>)

    @Query("DELETE FROM Registro WHERE deleted = 1")
    suspend fun clearDeleted()
}
