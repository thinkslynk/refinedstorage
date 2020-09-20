package com.refinedmods.refinedstorage.integration.rei

import net.fabricmc.loader.api.FabricLoader


object ReiIntegration {
    val isLoaded: Boolean by lazy {
        FabricLoader.getInstance().isModLoaded("roughlyenoughitems")
    }
}