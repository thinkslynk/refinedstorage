package com.refinedmods.refinedstorage.integration.rei

import net.fabricmc.loader.api.FabricLoader


object ReiIntegration {
    val isLoaded: Boolean
        get() = FabricLoader.getInstance().isModLoaded("roughlyenoughitems")

}