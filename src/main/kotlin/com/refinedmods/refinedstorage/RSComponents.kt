package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.block.CableBlock
import com.refinedmods.refinedstorage.block.ConstructorBlock
import com.refinedmods.refinedstorage.block.ControllerBlock
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
        listOf(
            CableBlock.ID,
            ConstructorBlock.ID,
            ControllerBlock.ID,
            ControllerBlock.CREATIVE_ID
        ).forEach {
            registry.registerFor(
                Identifier(RS.ID, it),
                NETWORK_NODE_PROXY,
                INetworkNodeProxy.Factory
            )
        }
    }
}