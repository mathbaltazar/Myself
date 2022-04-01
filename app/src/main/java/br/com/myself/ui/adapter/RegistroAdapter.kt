package br.com.myself.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.model.entity.Registro
import br.com.myself.model.repository.RegistroRepository
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.financas.registros.DetalhesRegistroDialog
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.adapter_registros_item.view.*

class RegistrosAdapter(private val repository: RegistroRepository) : ListAdapter<Registro, RecyclerView.ViewHolder>(
    object: DiffUtil.ItemCallback<Registro>() {
        override fun areItemsTheSame(oldItem: Registro, newItem: Registro): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Registro, newItem: Registro): Boolean = oldItem == newItem
    }
) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RegistroViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_registros_item, parent,false))
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RegistroViewHolder).bind(getItem(position))
    }
    
    
    private inner class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(registro: Registro) {
            
            itemView.tv_registro_descricao.text = registro.descricao
            itemView.tv_registro_valor.text = Utils.formatCurrency(registro.valor)
            itemView.tv_registro_data.text = registro.data!!.time.formattedDate()
            itemView.tv_registro_outros.text = registro.outros
            
            itemView.tv_registro_outros.visibility =
                if (registro.outros.isNullOrBlank()) View.GONE else View.VISIBLE
            
            itemView.iv_icon_registro_item_despesa_atrelada.visibility =
                if (registro.despesa_id == null || registro.despesa_id == 0L) View.INVISIBLE else View.VISIBLE
            
            //TODO HABILITAR ÍCONE QUANDO REGISTRO HOUVER ANEXO
            /*itemView.iv_icon_registro_item_anexo.visibility =
                if (registro.anexo == null) View.GONE else View.VISIBLE*/
            
            itemView.setOnClickListener {
                val dialog = DetalhesRegistroDialog(it.context, registro)
                dialog.show()
            }
            
            itemView.setOnLongClickListener {
                var mensagem = "Descrição: ${registro.descricao}"
                mensagem += "\nValor: ${Utils.formatCurrency(registro.valor)}"
                
                AlertDialog.Builder(it.context).setTitle("Excluir registro?").setMessage(mensagem)
                    .setPositiveButton("Excluir") { _, _ ->
                        repository.excluirRegistro(registro)
                        Trigger.launch(Events.Toast("Removido!"), Events.UpdateRegistros)
                    }.setNegativeButton("Cancelar", null).show()
                true
            }
            
        }
        
        
    }
    
}
