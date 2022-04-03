package br.com.myself.util

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.getStackTraceString

class Async {
    companion object {
        
        private val defaultObserver = object : Observer<Any> {
            override fun onComplete() {}
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: Any) {}
            override fun onError(e: Throwable) {
                e.printStackTrace()
                Log.e("Async | doInBackground", e.getStackTraceString())
            }
        }
        
        
        fun doInBackground(execute: () -> Unit) {
            Observable.fromCallable { execute() }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(defaultObserver)
        }
        
        fun <T> doInBackground(execute: () -> T, callback: (T) -> Unit) {
            Observable.fromCallable { execute() }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<T?> {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(t: T) {
                        Log.d("Async", "doInBackground onNext parameter : $t")
                        callback(t)
                    }
                    override fun onError(e: Throwable) {
                        defaultObserver.onError(e)
                    }
                })
        }
    }
}

