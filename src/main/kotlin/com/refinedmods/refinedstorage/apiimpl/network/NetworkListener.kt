package com.refinedmods.refinedstorage.apiimpl.network

import com.refinedmods.refinedstorage.apiimpl.API
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.world.ServerWorld

class NetworkListener : ServerTickEvents.EndWorldTick {
    override fun onEndTick(world: ServerWorld) {
        // TODO Profiler
        for (network in API.getNetworkManager(world).all()) {
            network.update()
        }

        for (node in API.getNetworkNodeManager(world).all()) {
            node.update()
        }
    }
}