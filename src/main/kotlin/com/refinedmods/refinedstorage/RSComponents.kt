package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import net.minecraft.util.Identifier


@Suppress("UnstableApiUsage")
class RSComponents : BlockComponentInitializer {
    companion object {
        val NETWORK_NODE_PROXY: ComponentKey<INetworkNodeProxy<*>> =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                Identifier(RS.ID, INetworkNodeProxy.ID),
                INetworkNodeProxy::class.java
            )
    }

    override fun registerBlockComponentFactories(registry: BlockComponentFactoryRegistry) {

        registry.registerFor(
            NetworkNodeTile::class.java,
            NETWORK_NODE_PROXY
        ) { it }
    }
}