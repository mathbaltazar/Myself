package br.com.myself.ui.financas.registros

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import br.com.myself.databinding.DialogDetalhesRegistroBinding
import br.com.myself.data.model.Registro
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate

class DetalhesRegistroDialog(context: Context, registro: Registro) : Dialog(context) {
    
    companion object {
        const val ACTION_EDITAR = 1
        const val ACTION_EXCLUIR = 2
    }
    
    private var mListener : ((Int, Registro) -> Unit)? = null
    private val binding: DialogDetalhesRegistroBinding
    
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        binding = DialogDetalhesRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
    
        binding.buttonEditar.setOnClickListener {
            mListener?.invoke(ACTION_EDITAR, registro)
        }
        binding.buttonExcluir.setOnClickListener {
            mListener?.invoke(ACTION_EXCLUIR, registro)
        }
        
        bindData(registro)
    }
    
    fun bindData(registro: Registro) {
        binding.textviewDescricao.text = registro.descricao
        binding.textviewData.text = registro.data.formattedDate()
        binding.textviewValor.text = Utils.formatCurrency(registro.valor)
        binding.textviewOutros.text = registro.outros
    
        binding.textviewReferenciaDespesa.visibility =
            if (registro.despesa_id != null) View.VISIBLE else  View.GONE
    }
    
    fun setOnActionListener(listener: (Int, Registro) -> Unit) {
        mListener = listener
    }
    
}
