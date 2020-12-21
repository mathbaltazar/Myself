package com.baltazarstudio.regular.ui.registros.movimentos

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.dialog_detalhes_movimento.*

class DetalhesMovimentoDialog(
    context: Context, private var movimento: Movimento
) : Dialog(context) {
    
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setUpView()
        show()
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_detalhes_movimento)
        bindData()
        
        button_detalhes_movimento_alterar.setOnClickListener {
            val dialog = RegistrarMovimentoDialog(context)
            dialog.edit(movimento) { movimento ->
                this.movimento = movimento
                bindData()
            }
            dialog.show()
        }
        
        button_detalhes_movimento_excluir.setOnClickListener {
            AlertDialog.Builder(context).setTitle("Excluir")
                .setMessage("Deseja realmente excluir este registro?")
                .setPositiveButton("Excluir") { _, _ ->
                    MovimentoContext.getDAO(context).excluir(movimento)
                    
                    Trigger.launch(TriggerEvent.Toast("Removido!"))
                    Trigger.launch(TriggerEvent.UpdateTelaMovimento())
                    Trigger.launch(TriggerEvent.UpdateTelaDespesa())
                    cancel()
                }.setNegativeButton("Cancelar", null).show()
        }
    }
    
    private fun bindData() {
        tv_detalhes_movimento_descricao.text = movimento.descricao
        tv_detalhes_movimento_data.text = movimento.data?.formattedDate()
        tv_detalhes_movimento_valor.text = Utils.formatCurrency(movimento.valor)
        
        if (movimento.referenciaDespesa != null && movimento.referenciaDespesa != 0) {
            if (verificarSeDepesaAindaExiste(movimento.referenciaDespesa!!))
                tv_detalhes_movimento_referencia_despesa.visibility = View.VISIBLE
        }
    }
    
    private fun verificarSeDepesaAindaExiste(codigo: Int): Boolean {
        val despesa = DespesaContext.getDAO(context).getDespesaPorCodigo(codigo)
        return despesa != null
    }
}
