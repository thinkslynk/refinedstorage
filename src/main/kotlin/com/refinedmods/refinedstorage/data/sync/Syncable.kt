package com.refinedmods.refinedstorage.data.sync

import io.netty.buffer.PooledByteBufAllocator
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.util.Identifier

interface Syncable<T>: PacketConsumer, SimpleObservable where T: Trackable<T>, T: SimpleObservable  {
    val identifier: Identifier
    val isClient: Boolean
    var data: T

    companion object{
        internal val byteBuffers: PooledByteBufAllocator =  PooledByteBufAllocator.DEFAULT
    }

    // These are only here so that they can be mocked
    fun getClientRegistry(): ClientSidePacketRegistry = ClientSidePacketRegistry.INSTANCE
    fun getServerRegistry(): ServerSidePacketRegistry = ServerSidePacketRegistry.INSTANCE

    fun send()

    fun register() {}
    fun unregister() {}

}