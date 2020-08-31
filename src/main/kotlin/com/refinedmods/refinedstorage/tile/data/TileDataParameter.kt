package com.refinedmods.refinedstorage.tile.data

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.network.PacketByteBuf

class TileDataParameter<T: Any, in E : BlockEntity>(
        var value: T,
        val serializer: TrackedDataHandler<T>,
        val valueProducer: (E)->T,
        val valueConsumer: ((E, T) -> Unit)? = null,
        val listener: TileDataParameterClientListener<T>? = null
) {
    var id = 0

    fun setValue(initial: Boolean, value: T) {
        this.value = value
        listener?.onChanged(initial, value)
    }

    fun setValueFromBuffer(initial: Boolean, buffer: PacketByteBuf) {
        setValue(initial, serializer.read(buffer))
    }

}