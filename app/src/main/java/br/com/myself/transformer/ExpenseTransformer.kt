package br.com.myself.transformer

import br.com.myself.data.dto.RegistroDTO
import br.com.myself.data.model.Registro
import br.com.myself.util.Utils.Companion.formattedDate

class ExpenseTransformer {
    companion object {
        fun toDTO(registro: Registro): RegistroDTO {
            return RegistroDTO(id = registro.serverId,
                descricao = registro.descricao,
                valor = 0.0,
                data = registro.data.formattedDate("yyyy-MM-dd"),
                outros = registro.outros,
                isDeleted = registro.isDeleted)
        }
        
    }
}