package com.refinedmods.refinedstorage.config

import com.refinedmods.refinedstorage.RS
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Config(name = RS.ID)
class RSConfig : PartitioningSerializer.GlobalData() {
    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    val serverConfig: ServerConfig = ServerConfig()

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    val clientConfig: ClientConfig = ClientConfig()
}