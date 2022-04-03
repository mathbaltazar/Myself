package br.com.myself.util

class AdapterClickListener<T>(
    val onClick: (T) -> Unit = {}, val onLongClick: (T) -> Unit = {}
)
