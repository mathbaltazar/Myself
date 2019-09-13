package com.baltazarstudio.regular.ui.pendencia

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.MenuInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.PendenciaDAO
import com.baltazarstudio.regular.model.Pendencia
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_detalhes_pendencia.*

class DetalhesPendenciaDialog(context: Context, private var item: Pendencia) : Dialog(context) {

    init {
        onCreate()
    }

    private fun onCreate() {
        setContentView(R.layout.dialog_detalhes_pendencia)
        toolbar_dialog_detalhes_pendencia.title = context.getString(R.string.dialog_title_detalhes_pendencia)

        setupMenu()

        button_detalhes_pendencia_marcar_como_pago.setOnClickListener {
            marcarComoPago()
        }

        bind()
    }

    private fun setupMenu() {
        val menu = toolbar_dialog_detalhes_pendencia.menu
        MenuInflater(context).inflate(R.menu.menu_dialog_detalhes_pendencia, menu)

        toolbar_dialog_detalhes_pendencia.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_pendencia_editar) {
                editar()
            } else if (it.itemId == R.id.action_pendencia_excluir) {
                confirmDialogExcluir()
            }
            true
        }
    }

    private fun confirmDialogExcluir(): Boolean {
        AlertDialog.Builder(context)
                .setTitle(R.string.all_dialog_title_excluir)
                .setMessage(R.string.all_dialog_message_excluir)
                .setPositiveButton(R.string.all_string_sim) { _, _ ->
                    PendenciaDAO(context).excluir(item)
                    Toast.makeText(context, R.string.toast_pendencia_removida, Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .setNegativeButton(R.string.all_string_nao, null)
                .show()
        return true
    }

    private fun editar() {
        val dialog = PendenciaCreateDialog(context).edit(item)
        dialog.setOnDismissListener {
            //item = PendenciaDAO(context).get(item.id!!)
            bind()
        }
        dialog.show()

    }


    private fun bind() {
        tv_item_pendencia_valor.text = Utils.formatCurrency(item.valor)
        tv_item_carteira_data.text = item.data
        tv_item_pendencia_descricao.text = item.descricao
    }

    private fun marcarComoPago() {
        AlertDialog.Builder(context)
                .setTitle(R.string.all_string_confirmar)
                .setMessage(R.string.dialog_mensagem_detalhe_pendencia_pago)
                .setPositiveButton(R.string.all_string_sim) { _: DialogInterface, _: Int ->
                    PendenciaDAO(context).definirComoPago(item)
                    Toast.makeText(context, R.string.toast_detalhes_pendencia_pago, Toast.LENGTH_LONG).show()
                    dismiss()
                }
                .setNegativeButton(R.string.all_string_nao) { _: DialogInterface, _: Int -> }
                .create()
                .show()
    }
}
