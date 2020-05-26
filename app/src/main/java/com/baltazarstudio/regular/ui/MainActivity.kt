package com.baltazarstudio.regular.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.BackupService
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.PagerAdapter
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.notification.Notification
import com.baltazarstudio.regular.ui.pendencia.MovimentosFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        tab_layout.setupWithViewPager(vp_content_main)

        val adapter = PagerAdapter(supportFragmentManager)
        val fragment = MovimentosFragment()
        adapter.addFragment(fragment, "Movimentos")
        vp_content_main.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.66:8080") //// LOCALIZAR IP DO SERVIDOR !!
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()


        val service = retrofit.create(BackupService::class.java)
        service.restoreDataObservable().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.w("OBSERVABLE SUCCESS RESPONSE", it.toString())
            }, {
                Log.w("OBSERVABLE ERROR RESPONSE", it.toString())
                it.printStackTrace()
            }).apply { }

        service.restoreDataCall().enqueue(object : Callback<List<Movimento>> {
            override fun onFailure(call: Call<List<Movimento>>, t: Throwable) {
                Log.w("CALL ERROR RESPONSE", t.toString())
                t.printStackTrace()
            }

            override fun onResponse(call: Call<List<Movimento>>, response: Response<List<Movimento>>) {
                Log.w("CALL SUCCESS RESPONSE", response.body().toString())
            }
        })
        Notification.createNotificationChannel(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //R.id.action_settings ->
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        Notification.notificar(this)
    }
}
