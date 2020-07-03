package com.baltazarstudio.regular.ui.backup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.ConfiguracaoDAO
import com.baltazarstudio.regular.database.dao.MovimentoDAO
import com.baltazarstudio.regular.model.Configuracao
import com.baltazarstudio.regular.service.BackupService
import com.baltazarstudio.regular.service.ConnectionTestService
import com.baltazarstudio.regular.service.dto.SincronizarDadosBackupDTO
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dados_backup.*
import okhttp3.HttpUrl
import org.jetbrains.anko.alert
import org.jetbrains.anko.contentView
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class DadosBackupActivity : AppCompatActivity() {

    companion object {

        private const val mUrl = "192.168.1.66"
        private const val mPorta = "8080"

        private const val FUNCAO_SINCRONIZAR = "funcao_sincronizar"
        private const val FUNCAO_RESTAURAR = "funcao_restaurar"
    }

    private lateinit var mConfiguracao: Configuracao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dados_backup)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Backup"

        mConfiguracao = ConfiguracaoDAO(this).getUltimaConfiguracao()

        textinput_backup_url.setText(mConfiguracao.url ?: mUrl)
        textinput_backup_porta.setText(mConfiguracao.porta ?: mPorta)


        button_backup_testar_conexao.setOnClickListener {
            conectar("")
        }

        tv_backup_teste_ultima_sincronizacao.text =
            String.format(
                "Última sincronização: %s",
                mConfiguracao.dataUltimaSincronizacao ?: "Não Disponível"
            )

        button_backup_sincronizar_dados.setOnClickListener {
            alert {
                title = "Sincronizar"
                message =
                    "Sincronizar todos os seus dados atuais? (Todos os dados do servidor serão substuidos por estes)"
                yesButton {
                    conectar(FUNCAO_SINCRONIZAR)
                }
                noButton { }
            }.show()
        }

        button_backup_restaurar.setOnClickListener {
            alert {
                title = "Atenção"
                message =
                    "Restaurar dados do servidor? (Todos os dados do dispositivo serão apagados!)" +
                            "" +
                            ")"
                yesButton {
                    conectar(FUNCAO_RESTAURAR)
                }
                noButton { }
            }.show()
        }
    }

    private fun conectar(funcao: String) {
        if (!isUrlValida()) return


        tv_backup_teste_conexao_mensagem.visibility = View.GONE
        progress_backup_loading.visibility = View.VISIBLE
        button_backup_testar_conexao.isEnabled = false
        button_backup_sincronizar_dados.isEnabled = false


        val url = "http://${textinput_backup_url.text}:${textinput_backup_porta.text}"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        val service = retrofit.create(ConnectionTestService::class.java)
        service.test().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progress_backup_loading.visibility = View.GONE
                button_backup_testar_conexao.isEnabled = true
                button_backup_sincronizar_dados.isEnabled = true

                tv_backup_teste_conexao_mensagem.visibility = View.VISIBLE
                tv_backup_teste_conexao_mensagem.text = "OK, tudo certo!"
                tv_backup_teste_conexao_mensagem.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.icon_confirmation,
                    0,
                    0,
                    0
                )

                mConfiguracao.url = textinput_backup_url.text.toString()
                mConfiguracao.porta = textinput_backup_porta.text.toString()
                ConfiguracaoDAO(this).salvarConfiguracao(mConfiguracao)

                when (funcao) {
                    FUNCAO_RESTAURAR -> restaurarDadosDoServidor()
                    FUNCAO_SINCRONIZAR -> sincronizarDados()
                }

            }, { error ->
                error.printStackTrace()
                erroResponse("Falha ao conectar com o serivdor!")
            }).apply { }

    }

    private fun sincronizarDados() {
        progress_backup_loading.visibility = View.VISIBLE
        button_backup_testar_conexao.isEnabled = false
        button_backup_sincronizar_dados.isEnabled = false


        val request = SincronizarDadosBackupDTO()
        request.movimentos = MovimentoDAO(this).getTodosMovimentos()
        request.configuracao = mConfiguracao
        request.configuracao!!.dataUltimaSincronizacao =
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(Date())

        val service = createBackupService()
        service.sincronizarDados(request).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progress_backup_loading.visibility = View.GONE
                button_backup_testar_conexao.isEnabled = true
                button_backup_sincronizar_dados.isEnabled = true

                Snackbar.make(contentView!!, "Os dados foram salvos!", Snackbar.LENGTH_LONG)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                    .show()

                tv_backup_teste_ultima_sincronizacao.text =
                    String.format(
                        "Última sincronização: %s",
                        mConfiguracao.dataUltimaSincronizacao ?: "Não Disponível"
                    )

            }, { error ->
                error.printStackTrace()
                erroResponse("Erro ao sincronizar dados")

            }).apply { }
    }

    private fun restaurarDadosDoServidor() {
        progress_backup_loading.visibility = View.VISIBLE
        button_backup_testar_conexao.isEnabled = false
        button_backup_sincronizar_dados.isEnabled = false


        val service = createBackupService()
        service.restaurarDados().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ t ->
                progress_backup_loading.visibility = View.GONE
                button_backup_testar_conexao.isEnabled = true
                button_backup_sincronizar_dados.isEnabled = true

                val dto = t.body()!!
                MovimentoDAO(this).restoreData(dto.movimentos)
                ConfiguracaoDAO(this).salvarConfiguracao(dto.configuracao)

            }, { error ->
                error.printStackTrace()
                erroResponse("Erro ao restaurar dados")
            }).apply { }

    }


    private fun createBackupService(): BackupService {
        val url = "http://${textinput_backup_url.text}:${textinput_backup_porta.text}/"

        val retrofit = Retrofit.Builder()
            .baseUrl(url) // Versão Futura: LOCALIZAR IP DO SERVIDOR
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(BackupService::class.java)
    }

    private fun isUrlValida(): Boolean {
        val url = "http://${textinput_backup_url.text}:${textinput_backup_porta.text}"

        try {
            HttpUrl.get(url)
        } catch (ex: IllegalArgumentException) {
            tv_backup_teste_conexao_mensagem.visibility = View.VISIBLE
            tv_backup_teste_conexao_mensagem.text = "IP ou Porta inválidos!"
            tv_backup_teste_conexao_mensagem.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.icon_error,
                0,
                0,
                0
            )
            return false
        }

        return true
    }


    private fun erroResponse(mensagem: String) {

        tv_backup_teste_conexao_mensagem.text = mensagem
        tv_backup_teste_conexao_mensagem.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.icon_error,
            0,
            0,
            0
        )
        tv_backup_teste_conexao_mensagem.visibility = View.VISIBLE


        progress_backup_loading.visibility = View.GONE
        button_backup_testar_conexao.isEnabled = true
        button_backup_sincronizar_dados.isEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        conectar("")
    }
}