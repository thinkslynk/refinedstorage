package com.refinedmods.refinedstorage.data.sync

import net.fabricmc.fabric.api.network.PacketConsumer
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.util.Identifier

interface Syncable<T: SimpleObservable>: PacketConsumer {
    val identifier: Identifier
    val isClient: Boolean
    var data: T
    val serializer: TrackedDataHandler<T>

    fun send()

    fun registerClient() {}
    fun registerServer() {}

    fun unregisterClient() {}
    fun unregisterServer() {}
}