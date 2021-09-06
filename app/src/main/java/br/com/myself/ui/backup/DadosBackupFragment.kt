package br.com.myself.ui.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.com.myself.R
import br.com.myself.context.ConfigContext
import br.com.myself.context.DespesaContext
import br.com.myself.context.EntradaContext
import br.com.myself.context.RegistroContext
import br.com.myself.database.dao.BackupDAO
import br.com.myself.model.Backup
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.service.BackupService
import br.com.myself.service.ConnectionTestService
import br.com.myself.service.dto.SincronizarDadosBackupDTO
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_dados_backup.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class DadosBackupFragment : Fragment() {

    companion object {

        private const val mUrl = "192.168.1.66"
        private const val mPorta = "8080"

        private const val FUNCAO_SINCRONIZAR = "funcao_sincronizar"
        private const val FUNCAO_RESTAURAR = "funcao_restaurar"
    }
    
    private lateinit var mView: View
    private lateinit var mBackup: Backup
    private lateinit var httpClient: OkHttpClient
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_dados_backup, container, false)
        return mView
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBackup = BackupDAO(mView.context).getUltimoBackup()

        textinput_backup_url.setText(mBackup.url ?: mUrl)
        textinput_backup_porta.setText(mBackup.porta ?: mPorta)


        button_backup_testar_conexao.setOnClickListener {
            conectar("")
        }

        
        val lastSync = mBackup.dataUltimaSincronizacao
        if (lastSync != null && lastSync != 0L) {
            val data = Date(lastSync)
            tv_backup_teste_ultima_sincronizacao.text =
                "Última sincronização: ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(data)}"
        } else {
            tv_backup_teste_ultima_sincronizacao.text = "Última sincronização: Não Disponível"
        }
        

        button_backup_sincronizar_dados.setOnClickListener {
            AlertDialog.Builder(mView.context).setTitle("Sincronizar")
                .setMessage("Sincronizar todos os seus dados atuais? (Todos os dados do servidor serão substuidos por estes)")
                .setPositiveButton("Sincronizar") { _, _ -> conectar(FUNCAO_SINCRONIZAR) }
                .setNegativeButton("Cancelar") { _, _ -> }
                .create()
                .show()
        }

        button_backup_restaurar.setOnClickListener {
            AlertDialog.Builder(mView.context).setTitle("Atenção")
                .setMessage("Restaurar dados do servidor?")
                .setPositiveButton("Restaurar") { _, _ -> conectar(FUNCAO_RESTAURAR) }
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
                progress_backup_loading.visibility = View.INVISIBLE
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

                mBackup.url = textinput_backup_url.text.toString()
                mBackup.porta = textinput_backup_porta.text.toString()
                BackupDAO(mView.context).salvarConfiguracao(mBackup)

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
        request.registros = RegistroContext.getDAO(mView.context).getTodosRegistros()
        request.despesas = DespesaContext.getDAO(mView.context).getTodasDespesas()
        request.entradas = EntradaContext.getDAO(mView.context).getTodasEntradas()
        request.backup = mBackup
        request.backup!!.dataUltimaSincronizacao = Calendar.getInstance().timeInMillis

        val service = createBackupService()
        service.sincronizarDados(request).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progress_backup_loading.visibility = View.INVISIBLE
                button_backup_testar_conexao.isEnabled = true
                button_backup_sincronizar_dados.isEnabled = true
                button_backup_restaurar.isEnabled = true

                Snackbar.make(mView, "Os dados foram salvos!", Snackbar.LENGTH_LONG)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                    .show()
    
                val lastSync = mBackup.dataUltimaSincronizacao
                if (lastSync != null && lastSync != 0L) {
                    val data = Date(lastSync)
                    tv_backup_teste_ultima_sincronizacao.text =
                        "Última sincronização: ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(data)}"
                } else {
                    tv_backup_teste_ultima_sincronizacao.text = "Última sincronização: Não Disponível"
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
                progress_backup_loading.visibility = View.INVISIBLE
                button_backup_testar_conexao.isEnabled = true
                button_backup_sincronizar_dados.isEnabled = true
                button_backup_restaurar.isEnabled = true

                val dto = t.body()!!
                RegistroContext.getDAO(mView.context).restaurarRegistros(dto.registros)
                DespesaContext.getDAO(mView.context).restaurarDespesas(dto.despesas)
                EntradaContext.getDAO(mView.context).restaurarEntradas(dto.entradas)
                ConfigContext.getDAO(mView.context).salvarConfiguracao(dto.backup)
                
                Trigger.launch(Events.Toast("Os dados do servidor foram restaurados!"))

            }, { error ->
                error.printStackTrace()
                erroResponse("Erro ao restaurar dados")
            }).apply { }

    }


    private fun createBackupService(): BackupService {
        val url = "http://${textinput_backup_url.text}:${textinput_backup_porta.text}/"

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
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


        progress_backup_loading.visibility = View.INVISIBLE
        button_backup_testar_conexao.isEnabled = true
        button_backup_sincronizar_dados.isEnabled = true
    }

    override fun onStart() {
        super.onStart()
        conectar("")
    }
}