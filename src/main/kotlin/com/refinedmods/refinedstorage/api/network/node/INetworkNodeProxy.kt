package com.refinedmods.refinedstorage.api.network.node

import dev.onyxstudios.cca.api.v3.block.BlockComponent
import dev.onyxstudios.cca.api.v3.block.BlockComponentProvider
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView




/**
 * Makes a network node accessible from a tile entity. Implement this as a capability.
 *
 * @param <T> the network node
</T> */
interface INetworkNodeProxy<T : INetworkNode>:BlockComponent {
    companion object {
        const val ID = "network_node_proxy"
    }

    /**
     * Returns the node.
     * Needs to work on the client and the server.
     * If there is no node present, don't silently return null but throw an exception since the game is in a bad state if that happens.
     *
     * @return the node
     */
    val node: T

    override fun writeToNbt(p0: CompoundTag) {
        // NO OP
    }
    override fun readFromNbt(p0: CompoundTag) {
        // NO OP
    }
    object Factory: BlockComponentProvider<INetworkNodeProxy<*>> {
        override fun get(p0: BlockState, p1: BlockView, p2: BlockPos, p3: Direction?): INetworkNodeProxy<*> {
            return object : INetworkNodeProxy<INetworkNode> {
                override val node: INetworkNode
                    get() = throw UnsupportedOperationException("Cannot use default implementation")
            }
        }
    }
}