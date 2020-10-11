package com.baltazarstudio.regular.observer

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class Trigger {
    companion object {
        
        private val trigger = PublishSubject.create<Any>()
        
        fun launch(t: Any) {
            trigger.onNext(t)
        }
        
        fun watcher(): Observable<Any> {
            return trigger
        }
        
    }
}