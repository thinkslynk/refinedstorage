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
        val ret = if(!world!!.isClient) {
            API.getNetworkNodeManager(world as ServerWorld).getCachedNode(pos) as N?
        } else null

        ret ?: createNode(world!!, pos)
    }
    override var markedForRemoval: Boolean = false // TODO Remove after updating references to check node
    override var redstoneMode: RedstoneMode
        get() = node.redstoneMode
        set(value) { node.redstoneMode = value }

    companion object {
        protected val log = getCustomLogger(NetworkNodeTile::class)
        @JvmField val REDSTONE_MODE: TileDataParameter<Int, NetworkNodeTile<*>> = createParameter()
    }

    init {
        dataManager.addWatchedParameter(REDSTONE_MODE)
    }

    abstract fun createNode(world: World, pos: BlockPos): N
    override fun toTag(tag: CompoundTag): CompoundTag = CompoundTag()


    override fun markRemoved() {
        super.markRemoved()

        onServer{world->
            markedForRemoval = true
            API.getNetworkNodeManager(world).removeNode(pos)

        markedForRemoval = true
        node.markedForRemoval = true

        world?.let {
            if (!it.isClient) {
                API.getNetworkNodeManager(it as ServerWorld).removeNode(pos)

                node.network?.let { network ->
                    network.nodeGraph.invalidate(Action.PERFORM, it, network.position)
                }
            }
        }
    }

    override fun setLocation(world: World, pos: BlockPos) {
        super.setLocation(world, pos)
        register()
    }

    fun register() {

        onServer{world->
            log.info("Registering...")
            val manager = API.getNetworkNodeManager(world)
            manager.setNode(pos, node)
            manager.markDirty()
        }
    }
}