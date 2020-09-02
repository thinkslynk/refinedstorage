package com.refinedmods.refinedstorage.data

import com.refinedmods.refinedstorage.data.sync.SimpleObservable
import com.refinedmods.refinedstorage.data.sync.SimpleObserver
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class BaseBlockEntityData(
    var blockPos: BlockPos,
    var direction: Direction,
    override val observers: Collection<SimpleObserver> = emptyList()
): SimpleObservable {
    companion object{
        // TODO Add annotation to Fabric Seams for TrackedDataHandlers
        val SERIALIZER = object: TrackedDataHandler<BaseBlockEntityData>{
            override fun write(buf: PacketByteBuf, o: BaseBlockEntityData) {
                buf.writeBlockPos(o.blockPos)
                buf.writeEnumConstant(o.direction)
            }

            override fun read(buf: PacketByteBuf): BaseBlockEntityData {
                return BaseBlockEntityData(
                    blockPos = buf.readBlockPos(),
                    direction = buf.readEnumConstant(Direction::class.java)
                )
            }

            override fun copy(o: BaseBlockEntityData): BaseBlockEntityData {
                return o
            }
        }
    }
}