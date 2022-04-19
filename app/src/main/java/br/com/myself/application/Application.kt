package br.com.myself.application

import android.app.Application
import android.util.Log
import br.com.myself.R
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class Application : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("Comfortaa-VariableFont_wght.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build())
                ).build()
        )
        
        Log.d("ApplicationContext","onCreated disparado!")
    }
    
}