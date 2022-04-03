package br.com.myself.ui.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.context.CriseContext
import br.com.myself.model.entity.Crise
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.adapter_crises_item.view.*

class CrisesAdapter(private val context: Context, val onEditCriseListener: (Crise) -> Unit = {}) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val crises = CriseContext.criseDataView.crises
    
    override fun getItemCount(): Int {
        return crises.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(inflater.inflate(R.layout.adapter_crises_item, parent, false))
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(crises[position])
    }
    
    private inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(crise: Crise) {
            
            val menu = PopupMenu(context, itemView, Gravity.END).also { inflateLongClickMenu(it, crise) }
            itemView.setOnClickListener { menu.show() }
    
            itemView.tv_adapter_crises_item_data.text = crise.data.formattedDate()
            itemView.tv_adapter_crises_item_horario1.text = crise.horario1
            itemView.tv_adapter_crises_item_horario2.text = crise.horario2
            itemView.tv_adapter_crises_item_observacoes.text =
                if (crise.observacoes.isNullOrBlank()) "Nada registrado." else crise.observacoes
        }
        
        private fun inflateLongClickMenu(popup: PopupMenu, crise: Crise) {
            popup.menu.add("Editar").setOnMenuItemClickListener {
                onEditCriseListener(crise)
                true    
            }
            
            popup.menu.add("Excluir").setOnMenuItemClickListener {
                var mensagem = "Data: ${crise.data.formattedDate()}"
                mensagem += "\nHorários: Entre ${crise.horario1} e ${crise.horario2}"
                mensagem += "\nObservações: ${crise.observacoes}"
    
                AlertDialog.Builder(itemView.context).setTitle("Excluir crise?")
                    .setMessage(mensagem)
                    .setPositiveButton("Excluir") { _, _ ->
            
                        CriseContext.getDAO(itemView.context).excluir(crise)
                        CriseContext.removerCrise(crise)
                        Trigger.launch(Events.Toast("Removida!"), Events.UpdateCrises)
            
                    }.setNegativeButton("Cancelar", null)
                    .show()
                true
            }
        }
    }
}
