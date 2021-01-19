package com.baltazarstudio.regular.application

import android.app.Application
import com.baltazarstudio.regular.R
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
    }
}