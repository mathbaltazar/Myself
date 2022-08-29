package br.com.myself.data.api.utils

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiProvider {
    companion object {
        private const val DEFAULT_NETWORK_URL = "http://192.168.1.66:8080/myself/"

        private var mRetrofitClient: Retrofit? = null
        private val mClient =
            OkHttpClient()
                .newBuilder()
                .addInterceptor(BackendInterceptor())
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()
        
        
        private fun getRetrofitInstance(): Retrofit {
            return mRetrofitClient ?: Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(DEFAULT_NETWORK_URL)
                .client(mClient)
                .build().also { mRetrofitClient = it }
        }
        
        fun <T> get(service: Class<T>): T = synchronized(this) {
            return getRetrofitInstance().create(service)
        }
    }
    
}