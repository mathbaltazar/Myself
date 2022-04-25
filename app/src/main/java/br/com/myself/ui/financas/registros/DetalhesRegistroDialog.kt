package br.com.myself.ui.financas.registros

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import br.com.myself.R
import br.com.myself.domain.entity.Registro
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.dialog_detalhes_registro.*

class DetalhesRegistroDialog(context: Context, registro: Registro) : Dialog(context) {
    
    companion object {
        const val ACTION_EDITAR = 1
        const val ACTION_EXCLUIR = 2
    }
    
    private val disposable = CompositeDisposable()
    private var mListener : ((Int, Registro) -> Unit)? = null
    
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialog_detalhes_registro)
    
        button_detalhes_registro_alterar.setOnClickListener {
            mListener?.invoke(ACTION_EDITAR, registro)
        }
        button_detalhes_registro_excluir.setOnClickListener {
            mListener?.invoke(ACTION_EXCLUIR, registro)
        }
        
        bindData(registro)
    }
    
    fun bindData(registro: Registro) {
        tv_detalhes_registro_descricao.text = registro.descricao
        tv_detalhes_registro_data.text = registro.data.formattedDate()
        tv_detalhes_registro_valor.text = Utils.formatCurrency(registro.valor)
        tv_detalhes_registro_outros.text = registro.outros
    
        tv_detalhes_registro_referencia_despesa.visibility =
            if (registro.despesa_id != null) View.VISIBLE else  View.GONE
    }
    
    fun setOnActionListener(listener: (Int, Registro) -> Unit) {
        mListener = listener
    }
  
    override fun onStop() {
        disposable.clear()
        super.onStop()
    }
    
    
}
