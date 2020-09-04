package com.refinedmods.refinedstorage.data.sync

import java.lang.ref.WeakReference

interface SimpleObservable {
    val observers: HashSet<WeakReference<SimpleObserver>>
    fun notifyObservers() {
        observers.forEach {it.get()?.onUpdate()}
    }
}