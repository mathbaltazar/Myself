package br.com.myself.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.databinding.AdapterRegistroBinding
import br.com.myself.ui.adapter.state.UIModelState

class RegistroAdapter : ListAdapter<UIModelState, RecyclerView.ViewHolder>(COMPARATOR) {
    companion object {
        private object COMPARATOR : DiffUtil.ItemCallback<UIModelState>()  {
            override fun areItemsTheSame(oldItem: UIModelState, newItem: UIModelState): Boolean {
                return (oldItem is UIModelState.UIRegistroState && newItem is UIModelState.UIRegistroState && oldItem.id == oldItem.id)
            }
            override fun areContentsTheSame(oldItem: UIModelState, newItem: UIModelState): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RegistroViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_registro, parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RegistroViewHolder).bind(getItem(position) as UIModelState.UIRegistroState)
    }

    private inner class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AdapterRegistroBinding.bind(itemView)

        fun bind(registroModelState: UIModelState.UIRegistroState) {

            binding.textviewDescricao.text = registroModelState.descricao
            binding.textviewValor.text = registroModelState.valor
            binding.textviewData.text = registroModelState.data
            binding.textviewOutros.text = registroModelState.outros

            binding.textviewOutros.visibility =
                if (registroModelState.outros.isNullOrBlank()) View.GONE else View.VISIBLE

            binding.imageviewIconeSync.visibility =
                if (registroModelState.isSync) View.VISIBLE else View.INVISIBLE

            //TODO HABILITAR ÍCONE QUANDO REGISTRO HOUVER ANEXO
            /*itemView.iv_icon_registro_item_anexo.visibility =
                if (registro.anexo == null) View.INVISIBLE else View.VISIBLE*/

            binding.root.setOnClickListener {
                registroModelState.onItemSelected()
            }

        }
    }

}