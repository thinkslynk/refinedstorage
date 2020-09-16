package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import com.refinedmods.refinedstorage.extensions.onServer
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable
import com.refinedmods.refinedstorage.tile.config.RedstoneMode
import com.refinedmods.refinedstorage.tile.config.RedstoneMode.Companion.createParameter
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("UnstableApiUsage")
abstract class NetworkNodeTile<N : NetworkNode>(tileType: BlockEntityType<*>?) :
    BaseTile(tileType),
    INetworkNodeProxy<N>,
    IRedstoneConfigurable {
    override val node: N by lazy {
        createNode(world!!, pos)
    }
    override var markedForRemoval: Boolean = false
    override var redstoneMode: RedstoneMode
        get() {
            return node.redstoneMode
        }
        set(value) {
            node.redstoneMode = value
        }

    override fun markRemoved() {
        super.markRemoved()
        onServer{world->
            markedForRemoval = true
            API.getNetworkNodeManager(world).removeNode(pos)

            node.network?.let {
                it.nodeGraph.invalidate(Action.PERFORM, it.world, it.position)
            }
        }
    }

    override fun setLocation(world: World, pos: BlockPos) {
        super.setLocation(world, pos)
        register()
    }

    fun register() {
        onServer{world->
            API.getNetworkNodeManager(world).setNode(pos, node)
        }
    }

    abstract fun createNode(world: World, pos: BlockPos): N
    override fun toTag(tag: CompoundTag): CompoundTag = CompoundTag()

    companion object {
        val log = getCustomLogger(NetworkNodeTile::class)

        @JvmField
        val REDSTONE_MODE: TileDataParameter<Int, NetworkNodeTile<*>> = createParameter()
    }

    init {
        dataManager.addWatchedParameter(REDSTONE_MODE)
    }
}