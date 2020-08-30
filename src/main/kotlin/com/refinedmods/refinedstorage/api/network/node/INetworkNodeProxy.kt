package com.refinedmods.refinedstorage.api.network.node

import dev.onyxstudios.cca.api.v3.block.BlockComponent




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

    var markedForRemoval: Boolean
}