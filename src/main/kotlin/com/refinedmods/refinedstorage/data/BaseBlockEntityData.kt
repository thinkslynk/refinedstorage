package com.refinedmods.refinedstorage.data

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.data.sync.SimpleObservable
import com.refinedmods.refinedstorage.data.sync.SimpleObserver
import com.refinedmods.refinedstorage.data.sync.Trackable
import java.lang.ref.WeakReference
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class BaseBlockEntityData(
    blockPos: BlockPos = BlockPos.ORIGIN,
    direction: Direction? = null
): SimpleObservable, Trackable<BaseBlockEntityData> {
    override val observers: HashSet<WeakReference<SimpleObserver>> = hashSetOf()

    var blockPos: BlockPos = blockPos
        set(v) {
            field = v
            this.notifyObservers()
        }
    var direction: Direction? = direction
        set(v) {
            field = v
            this.notifyObservers()
        }

    companion object{
        val ID = Identifier(RS.ID, "base_block_entiy_data")
        // TODO Add annotation to Fabric Seams for TrackedDataHandlers
        val SERIALIZER = object: TrackedDataHandler<BaseBlockEntityData>{
            override fun write(buf: PacketByteBuf, o: BaseBlockEntityData) {
                buf.writeBlockPos(o.blockPos)
                buf.writeBoolean(o.direction != null)
                o.direction?.let{
                    buf.writeEnumConstant(it)
                }
            }

            override fun read(buf: PacketByteBuf): BaseBlockEntityData {
                val pos = buf.readBlockPos()
                val hasDirection = buf.readBoolean()
                val direction = if (hasDirection) {
                    buf.readEnumConstant(Direction::class.java)
                } else {
                    null
                }
                return BaseBlockEntityData(
                    blockPos = pos,
                    direction = direction
                )
            }

            override fun copy(o: BaseBlockEntityData): BaseBlockEntityData {
                return o
            }
        }
    }

    override fun getSerializer(): TrackedDataHandler<BaseBlockEntityData> {
        return SERIALIZER
    }
}