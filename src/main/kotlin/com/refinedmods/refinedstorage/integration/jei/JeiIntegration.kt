package com.refinedmods.refinedstorage.integration.jei

import net.fabricmc.loader.api.FabricLoader


object ReiIntegration {
    val isLoaded: Boolean
        get() = FabricLoader.getInstance().isModLoaded("rei")

}