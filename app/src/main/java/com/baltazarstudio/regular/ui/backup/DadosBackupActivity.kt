package com.baltazarstudio.regular.ui.backup

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.ConfigContext
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.context.EntradaContext
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.database.dao.ConfiguracaoDAO
import com.baltazarstudio.regular.model.Configuracao
import com.baltazarstudio.regular.service.BackupService
import com.baltazarstudio.regular.service.ConnectionTestService
import com.baltazarstudio.regular.service.dto.SincronizarDadosBackupDTO
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dados_backup.*
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.contentView
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
    private lateinit var httpClient: OkHttpClient

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

        
        val lastSync = mConfiguracao.dataUltimaSincronizacao
        if (lastSync != null && lastSync != 0L) {
            val data = Date(lastSync)
            tv_backup_teste_ultima_sincronizacao.text =
                SimpleDateFormat("dd/MM/yyyy HH:mm").format(data)
        } else {
            tv_backup_teste_ultima_sincronizacao.text = "Não Disponível"
        }
        

        button_backup_sincronizar_dados.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Sincronizar")
                .setMessage("Sincronizar todos os seus dados atuais? (Todos os dados do servidor serão substuidos por estes)")
                .setPositiveButton("Sincronizar") { _, _ -> conectar(FUNCAO_SINCRONIZAR) }
                .setNegativeButton("Cancelar") { _, _ -> }
                .create()
                .show()
        }

        button_backup_restaurar.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Atenção")
                .setMessage("Restaurar dados do servidor?")
                .setPositiveButton("Sincronizar") { _, _ -> conectar(FUNCAO_RESTAURAR) }
                .setNegativeButton("Cancelar") { _, _ -> }
                .create()
                .show()
        }
        
        val mInterceptor = HttpLoggingInterceptor()
        mInterceptor.level = HttpLoggingInterceptor.Level.BODY
        
        httpClient = OkHttpClient().newBuilder()
            .addInterceptor(mInterceptor)
            .build()
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
        button_backup_restaurar.isEnabled = false


        val request = SincronizarDadosBackupDTO()
        request.movimentos = MovimentoContext.getDAO(this).getTodosMovimentos()
        request.despesas = DespesaContext.getDAO(this).getTodasDespesas()
        request.entradas = EntradaContext.getDAO(this).getTodasEntradas()
        request.configuracao = mConfiguracao
        request.configuracao!!.dataUltimaSincronizacao = Calendar.getInstance().timeInMillis

        val service = createBackupService()
        service.sincronizarDados(request).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progress_backup_loading.visibility = View.GONE
                button_backup_testar_conexao.isEnabled = true
                button_backup_sincronizar_dados.isEnabled = true
                button_backup_restaurar.isEnabled = true

                Snackbar.make(contentView!!, "Os dados foram salvos!", Snackbar.LENGTH_LONG)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                    .show()
    
                val lastSync = mConfiguracao.dataUltimaSincronizacao
                if (lastSync != null && lastSync != 0L) {
                    val data = Date(lastSync)
                    tv_backup_teste_ultima_sincronizacao.text =
                        SimpleDateFormat("dd/MM/yyyy HH:mm").format(data)
                } else {
                    tv_backup_teste_ultima_sincronizacao.text = "Não Disponível"
                }

            }, { error ->
                error.printStackTrace()
                erroResponse("Erro ao sincronizar dados")
            }).apply { }
    }

    private fun restaurarDadosDoServidor() {
        progress_backup_loading.visibility = View.VISIBLE
        button_backup_testar_conexao.isEnabled = false
        button_backup_sincronizar_dados.isEnabled = false
        button_backup_restaurar.isEnabled = false


        val service = createBackupService()
        service.restaurarDados().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ t ->
                progress_backup_loading.visibility = View.GONE
                button_backup_testar_conexao.isEnabled = true
                button_backup_sincronizar_dados.isEnabled = true
                button_backup_restaurar.isEnabled = true

                val dto = t.body()!!
                MovimentoContext.getDAO(this).restaurarMovimentos(dto.movimentos)
                DespesaContext.getDAO(this).restaurarDespesas(dto.despesas)
                EntradaContext.getDAO(this).restaurarEntradas(dto.entradas)
                ConfigContext.getDAO(this).salvarConfiguracao(dto.configuracao)

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
            .client(httpClient)
            .build()

        return retrofit.create(BackupService::class.java)
    }

    private fun isUrlValida(): Boolean {
        val url = "http://${textinput_backup_url.text}:${textinput_backup_porta.text}"

        try {
            url.toHttpUrl()
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