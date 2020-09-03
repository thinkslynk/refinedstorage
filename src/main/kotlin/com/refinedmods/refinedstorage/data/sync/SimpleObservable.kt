package com.refinedmods.refinedstorage.data.sync

interface SimpleObservable: SimpleObserver {
    val observers: MutableList<SimpleObserver>
    override fun onUpdate() {
        observers.forEach {it.onUpdate()}
    }
}