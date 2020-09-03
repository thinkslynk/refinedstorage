package com.refinedmods.refinedstorage.data.sync

import net.fabricmc.fabric.api.network.PacketConsumer
import net.minecraft.util.Identifier

interface Syncable<T>: PacketConsumer where T: Trackable<T>, T: SimpleObservable  {
    val identifier: Identifier
    val isClient: Boolean
    var data: T

    fun send()

    fun registerClient() {}
    fun registerServer() {}

    fun unregisterClient() {}
    fun unregisterServer() {}
}