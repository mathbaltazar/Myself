package br.com.myself.application

import android.app.Application
import android.util.Log
import androidx.room.Room
import br.com.myself.R
import br.com.myself.model.database.MyDatabase
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class Application : Application() {
    
    private lateinit var myDatabase: MyDatabase
    
    override fun onCreate() {
        super.onCreate()
    
        myDatabase = Room.databaseBuilder(this, MyDatabase::class.java, MyDatabase.NAME).build()
        
        
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
    
    fun getDatabase() : MyDatabase {
        return myDatabase
    }
    
}