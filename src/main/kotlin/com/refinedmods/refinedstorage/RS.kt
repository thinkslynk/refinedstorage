package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.apiimpl.network.NetworkListener
import com.refinedmods.refinedstorage.config.RSConfig
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import com.refinedmods.refinedstorage.network.NetworkHandler
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.thinkslynk.fabric.generated.BlockEntityRegistryGenerated
import com.thinkslynk.fabric.generated.BlockItemRegistryGenerated
import com.thinkslynk.fabric.generated.BlockRegistryGenerated
import com.thinkslynk.fabric.generated.ItemRegistryGenerated
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents

class RS: ModInitializer {
    companion object{
        val log = getCustomLogger(RS::class)
        const val ID = "refinedstorage"
        val NETWORK_HANDLER = NetworkHandler()

        lateinit var CONFIG: RSConfig
    }

    override fun onInitialize() {
        AutoConfig.register(RSConfig::class.java) {
            definition: Config, configClass: Class<RSConfig> ->
            JanksonConfigSerializer(definition, configClass)
        }

        ItemRegistryGenerated.register()
        BlockRegistryGenerated.register()
        BlockItemRegistryGenerated.register()
        BlockEntityRegistryGenerated.register()
        RSSerializers.registerAll()

        NetworkHandler.register()

        ServerTickEvents.END_WORLD_TICK.register(NetworkListener())

        CONFIG = AutoConfig.getConfigHolder(RSConfig::class.java).config




        // TODO Register stuff!
//        DistExecutor.safeRunWhenOn(Dist.CLIENT, { { ClientSetup() } })
//        MinecraftForge.EVENT_BUS.register(ServerSetup())
//        val commonSetup = CommonSetup()
//        FMLJavaModLoadingContext.get().getModEventBus().addListener({ e: ? -> commonSetup.onCommonSetup(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block::class.java, { e:<net.minecraft.block.Block?> -> commonSetup.onRegisterBlocks(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(BlockEntityType::class.java, { e:<<>?> -> commonSetup.onRegisterTiles(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item::class.java, { e:<net.minecraft.item.Item?> -> commonSetup.onRegisterItems(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer::class.java, { e:<<>?> -> commonSetup.onRegisterRecipeSerializers(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType::class.java, { e:<<>?> -> commonSetup.onRegisterContainers(e) })
//        deliver()
        log.info("Initialized!")
    }
}