package com.baltazarstudio.regular.observer

import android.annotation.SuppressLint
import android.os.Handler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Trigger {
    companion object {
        
        private val trigger = PublishSubject.create<Any>()
        
        fun launch(vararg t: Any) {
            t.forEach { trigger.onNext(it) }
        }
        
        
        fun watcher(): Observable<Any> {
            return trigger
        }
        
    }
}