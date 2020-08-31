package com.refinedmods.refinedstorage.integration.modmenu

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.config.RSConfig
import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import java.util.function.Supplier

@Environment(EnvType.CLIENT)
class ModMenuIntegration : ModMenuApi {
    override fun getModId(): String {
        return RS.ID
    }

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>? {
        return ConfigScreenFactory<Screen> { parent: Screen ->
            AutoConfig.getConfigScreen(RSConfig::class.java, parent).get()
        }
    }
}