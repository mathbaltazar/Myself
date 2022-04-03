package br.com.myself.ui.financas.registros

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import br.com.myself.R
import br.com.myself.model.entity.Registro
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_detalhes_registro.*

class DetalhesRegistroDialog(context: Context, registro: Registro) : Dialog(context) {
    
    private val disposable = CompositeDisposable()
    
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialog_detalhes_registro)
        
        setUpView(registro)
        show()
    }
    
    override fun onStart() {
        registrarObservables()
        super.onStart()
    }
    
    private fun setUpView(registro: Registro) {
        tv_detalhes_registro_descricao.text = registro.descricao
        tv_detalhes_registro_data.text = registro.data.formattedDate()
        tv_detalhes_registro_valor.text = Utils.formatCurrency(registro.valor)
    
        tv_detalhes_registro_referencia_despesa.visibility =
            if (registro.despesa_id != null) View.VISIBLE else  View.GONE
        
        button_detalhes_registro_alterar.setOnClickListener {
            Trigger.launch(Events.EditarRegistro(registro))
        }
    }
    
    private fun registrarObservables() {
        disposable.add(Trigger.watcher().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { t ->
                if (t is Events.AtualizarDetalhesRegistro) {
                    setUpView(t.registro)
                }
            })
    }
    
    override fun onStop() {
        disposable.clear()
        super.onStop()
    }
    

}
