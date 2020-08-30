package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode
import com.refinedmods.refinedstorage.block.ConstructorBlock
import com.refinedmods.refinedstorage.extensions.DataSerializers
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.thinkslynk.fabric.annotations.registry.RegisterBlockEntity
import com.thinkslynk.fabric.generated.BlockEntityRegistryGenerated
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.BiConsumer
import java.util.function.Function

@RegisterBlockEntity(RS.ID, ConstructorBlock.ID, ["CONSTRUCTOR_BLOCK"])
class ConstructorTile : NetworkNodeTile<ConstructorNetworkNode>(BlockEntityRegistryGenerated.CONSTRUCTOR_TILE) {
    override fun createNode(world: World, pos: BlockPos): ConstructorNetworkNode {
        return ConstructorNetworkNode(world, pos)
    }

    companion object {
        val COMPARE: TileDataParameter<Int, ConstructorTile> = IComparable.createParameter()
        val TYPE: TileDataParameter<Int, ConstructorTile> = IType.createParameter()
        val DROP: TileDataParameter<Boolean, ConstructorTile> =
            TileDataParameter<Boolean, ConstructorTile>(false,DataSerializers.BOOLEAN,
                Function { it.node.isDrop },
                BiConsumer{ t, v ->
                    t.node.isDrop = v
                    t.node.markDirty()
                }
            )
    }

    init {
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(TYPE)
        dataManager.addWatchedParameter(DROP)
    }
}